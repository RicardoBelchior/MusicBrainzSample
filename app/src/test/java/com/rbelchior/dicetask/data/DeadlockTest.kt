package com.rbelchior.dicetask.data

import kotlinx.coroutines.runBlocking
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Authenticator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okio.IOException
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class DeadlockTest {

    private lateinit var server: MockWebServer

    // We need a reference to the client to use inside the Authenticator
    private lateinit var client: OkHttpClient
    private lateinit var client2: OkHttpClient

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `PROVE DEADLOCK - async requests with same client hang forever`() {
        // 1. Setup Server
        // First request returns 401
        server.enqueue(MockResponse.Builder().code(401).build())
        // Refresh request (queued second) returns 200
        server.enqueue(MockResponse.Builder().code(200).body("NewToken").build())
        // Retry of first request returns 200
        server.enqueue(MockResponse.Builder().code(200).body("Success").build())

        // 3. The "Bad" Authenticator
        val badAuthenticator = Authenticator { _, response ->
            println("Authenticator: 401 hit. Trying to refresh...")

            val latch = CountDownLatch(1)
            val refreshSuccess = AtomicBoolean(false)

            // SIMULATE RETROFIT SUSPEND CALL
            // Retrofit suspend functions use .enqueue() internally.
            val refreshRequest = Request.Builder().url(server.url("/refresh")).build()

            println("Authenticator: Enqueuing refresh request...")

            // DEADLOCK HAPPENS HERE:
            // We enqueue a 2nd request.
            // But Dispatcher says: "Running calls = 1. Max = 1. Queue this new one."
            // So this callback NEVER fires.
            client.newCall(refreshRequest)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        latch.countDown()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        println("Authenticator: Refresh executed!")
                        refreshSuccess.set(true)
                        response.close()
                        latch.countDown()
                    }
                })

            // We simulate "runBlocking" by waiting for the latch
            val finished = latch.await(2, TimeUnit.SECONDS)

            if (!finished) {
                println("Authenticator: TIMED OUT waiting for refresh!")
                // The deadlock is proven here. We are stuck.
                throw RuntimeException("Deadlock detected!")
            }

            if (refreshSuccess.get()) {
                response.request.newBuilder().header("Authorization", "NewToken").build()
            } else {
                null
            }
        }

        // 4. Build Client
        client = OkHttpClient.Builder()
            .dispatcher(Dispatcher().apply {
                maxRequests = 1 // This means only 1 async request can run at a time.
                maxRequestsPerHost = 1
            })
            .authenticator(badAuthenticator)
            .build()

        client2 = client.newBuilder()
            .dispatcher(Dispatcher().apply {
                maxRequests = 1
                maxRequestsPerHost = 1
            })
            .authenticator { _, _ -> null }
            .build()

        // 5. Run the Main Request ASYNCHRONOUSLY
        // We must use enqueue to trigger the Dispatcher limits
        val mainLatch = CountDownLatch(1)
        val deadlockOccurred = AtomicBoolean(false)

        val request = Request.Builder().url(server.url("/api")).build()

        println("Test: Enqueuing main request...")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // In a deadlock, we might catch the RuntimeException thrown above
                if (e.message?.contains("Deadlock") == true) {
                    deadlockOccurred.set(true)
                }
                mainLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                mainLatch.countDown()
            }
        })

        // Wait for the whole flow
        mainLatch.await(3, TimeUnit.SECONDS)

        if (deadlockOccurred.get()) {
            println("TEST PASSED: Deadlock successfully reproduced.")
        } else {
            fail("TEST FAILED: No deadlock occurred (Did the refresh somehow slip through?)")
        }
    }

}