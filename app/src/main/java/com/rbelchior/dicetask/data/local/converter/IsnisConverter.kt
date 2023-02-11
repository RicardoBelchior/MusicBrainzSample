package com.rbelchior.dicetask.data.local.converter

import androidx.room.TypeConverter
import com.rbelchior.dicetask.util.JsonStringConverter

object IsnisConverter {

    private val jsonConverter = JsonStringConverter()

    @TypeConverter
    fun fromDomainObject(area: List<String>?): String? {
        return jsonConverter.encodeToString(area)
    }


    @TypeConverter
    fun toDomainObject(stringValue: String?): List<String>? {
        return stringValue?.let { jsonConverter.decodeFromString(it) }
    }

}
