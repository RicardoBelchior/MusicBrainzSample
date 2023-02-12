package com.rbelchior.dicetask.data.local.converter

import androidx.room.TypeConverter
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.util.JsonStringConverter

object RelationsConverter {

    private val jsonConverter = JsonStringConverter()

    @TypeConverter
    fun fromDomainObject(area: List<Artist.Relation>?): String? {
        return jsonConverter.encodeToString(area)
    }


    @TypeConverter
    fun toDomainObject(stringValue: String?): List<Artist.Relation>? {
        return stringValue?.let { jsonConverter.decodeFromString(it) }
    }

}
