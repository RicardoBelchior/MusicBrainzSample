package com.rbelchior.dicetask.data.remote.util

import com.rbelchior.dicetask.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import logcat.logcat

private const val HTTP_TIMEOUT = 30000L

internal val ktorLogger = object : Logger {
    override fun log(message: String) {
        logcat("HttpClient") { message }
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal fun createHttpClient(engine: HttpClientEngine): HttpClient {
    val config: HttpClientConfig<*>.() -> Unit = {
        expectSuccess = true
        followRedirects = true

        install(HttpTimeout) {
            requestTimeoutMillis = HTTP_TIMEOUT
            connectTimeoutMillis = HTTP_TIMEOUT
            socketTimeoutMillis = HTTP_TIMEOUT
        }

        if (BuildConfig.DEBUG) {
            install(Logging) {
                level = LogLevel.INFO
                logger = ktorLogger
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
                isLenient = true
            })
        }

        defaultRequest {
            header("Accepting", ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            userAgent("DiceTaskApp-0.0.1")
        }
    }

    return HttpClient(engine, config)
}
