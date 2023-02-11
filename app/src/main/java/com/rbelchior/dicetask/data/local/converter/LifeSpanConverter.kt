package com.rbelchior.dicetask.data.local.converter

import androidx.room.TypeConverter
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.util.JsonStringConverter

object LifeSpanConverter {

    private val jsonConverter = JsonStringConverter()

    @TypeConverter
    fun fromDomainObject(area: Artist.LifeSpan?): String? {
        return jsonConverter.encodeToString(area)
    }


    @TypeConverter
    fun toDomainObject(stringValue: String?): Artist.LifeSpan? {
        return stringValue?.let { jsonConverter.decodeFromString(it) }
    }

}
