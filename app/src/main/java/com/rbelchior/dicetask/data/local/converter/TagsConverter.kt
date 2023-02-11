package com.rbelchior.dicetask.data.local.converter

import androidx.room.TypeConverter
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.util.JsonStringConverter

object TagsConverter {

    private val jsonConverter = JsonStringConverter()

    @TypeConverter
    fun fromDomainObject(area: List<Artist.Tag>?): String? {
        return jsonConverter.encodeToString(area)
    }


    @TypeConverter
    fun toDomainObject(stringValue: String?): List<Artist.Tag>? {
        return stringValue?.let { jsonConverter.decodeFromString(it) }
    }

}
