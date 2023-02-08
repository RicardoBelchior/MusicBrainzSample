package com.rbelchior.dicetask.data.mapper

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

object DateMapper {

    fun mapToString(instant: Instant?): String? {
        if (instant == null) return null

        return instant.toString()
    }

    fun mapToLocalDate(date: String?): LocalDate? {
        if (date.isNullOrEmpty()) return null

        return try {
            LocalDate.parse(date)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

}
