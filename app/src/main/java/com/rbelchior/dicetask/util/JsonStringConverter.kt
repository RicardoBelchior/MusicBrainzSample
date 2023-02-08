package com.rbelchior.dicetask.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import logcat.LogPriority
import logcat.logcat

class JsonStringConverter {

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
                Json.decodeFromString(it)
            } catch (e: IllegalArgumentException) {
                logcat(LogPriority.ERROR) { "Exception in encodeToString: ${e.message}" }
                null
            }
        }
    }
}
