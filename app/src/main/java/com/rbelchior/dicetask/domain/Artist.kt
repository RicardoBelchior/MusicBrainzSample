package com.rbelchior.dicetask.domain

import kotlinx.datetime.LocalDate

data class Artist(
    val id: String,
    val name: String,
    val type: String?,
    val area: Area?,
    val beginArea: Area?,
    val isnis: List<String>,
    val lifeSpan: LifeSpan?,
    val tags: List<Tag>,
    val wikiDescription: String?
) {
    data class Area(
        val id: String,
        val type: String,
        val name: String,
    )

    data class LifeSpan(
        val begin: String?,
        val ended: String?
    )

    data class Tag(
        val count: Int?,
        val name: String?
    )
}
