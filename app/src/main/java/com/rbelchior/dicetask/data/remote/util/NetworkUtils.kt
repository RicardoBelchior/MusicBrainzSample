package com.rbelchior.dicetask.data.remote.util

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.utils.io.errors.*

suspend inline fun <reified T> safeCall(
    block: () -> HttpResponse
): Result<T> {
    try {
        val response = block()
        val body = response.body<T>()
        return Result.success(body)
    } catch (e: ResponseException) {
        return when (e) {
            // 3xx responses
            is RedirectResponseException -> Result.failure(NetworkException.Redirect(e))
            // 4xx responses
            is ClientRequestException -> Result.failure(NetworkException.ClientRequest(e))
            // 5xx responses
            is ServerResponseException -> Result.failure(NetworkException.ServerError(e))
            // Anything else
            else -> Result.failure(NetworkException.UnknownResponse(e))
        }
    } catch (e: IOException) {
        return Result.failure(NetworkException.IO(e))
    } catch (e: Exception) {
        return Result.failure(NetworkException.UnknownError(e))
    }
}
