package com.rbelchior.dicetask.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import logcat.LogPriority
import logcat.logcat

@OptIn(ExperimentalSerializationApi::class)
class JsonStringConverter {

    val jsonDecoder: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    inline fun <reified T> encodeToString(value: T?): String? =
        value?.let {
            try {
                Json.encodeToString(Json.serializersModule.serializer(), value)
            } catch (e: IllegalArgumentException) {
                logcat(LogPriority.ERROR) { "Exception in encodeToString: ${e.message}" }
                null
            }
        }

    inline fun <reified T> decodeFromString(input: String?): T? {
        return input?.let {
            try {
                jsonDecoder.decodeFromString(it)
            } catch (e: IllegalArgumentException) {
                logcat(LogPriority.ERROR) { "Exception in encodeToString: ${e.message}" }
                null
            }
        }
    }
}
