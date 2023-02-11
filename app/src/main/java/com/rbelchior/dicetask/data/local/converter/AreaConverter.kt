package com.rbelchior.dicetask.data.local.converter

import androidx.room.TypeConverter
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.util.JsonStringConverter

object AreaConverter {

    private val jsonConverter = JsonStringConverter()

    @TypeConverter
    fun fromDomainObject(area: Artist.Area?): String? {
        return jsonConverter.encodeToString(area)
    }


    @TypeConverter
    fun toDomainObject(stringValue: String?): Artist.Area? {
        return stringValue?.let { jsonConverter.decodeFromString(it) }
    }

}
