package com.rbelchior.dicetask.data.remote.util

import io.ktor.client.plugins.*
import io.ktor.utils.io.errors.*

sealed class NetworkException(
    throwable: Throwable
) : Throwable(throwable) {

    /**
     * Base class for all response exceptions.
     */
    open class Response(
        private val responseException: ResponseException
    ) : NetworkException(responseException) {
        fun statusCode() = responseException.response.status.value
    }

    /**
     * 3xx response exceptions
     */
    data class Redirect(
        val exception: RedirectResponseException
    ) : Response(exception)

    /**
     * 4xx response exceptions
     */
    data class ClientRequest(
        val exception: ClientRequestException
    ) : Response(exception)

    /**
     * 5xx response exceptions
     */
    data class ServerError(
        val exception: ServerResponseException
    ) : Response(exception)

    /**
     * Any other response exception
     */
    data class UnknownResponse(
        val exception: ResponseException
    ) : Response(exception)

    /**
     * IOException, such as [HttpRequestTimeoutException] or [SocketTimeoutException]
     */
    data class IO(
        val exception: IOException
    ) : NetworkException(exception)

    /**
     * Any other exception.
     */
    data class UnknownError(
        val throwable: Throwable
    ) : NetworkException(throwable)
}
