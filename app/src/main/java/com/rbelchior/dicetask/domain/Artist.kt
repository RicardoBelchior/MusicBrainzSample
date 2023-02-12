package com.rbelchior.dicetask.domain

import android.net.Uri

data class Artist(
    val id: String,
    val name: String,
    val type: Type,
    val area: Area?,
    val beginArea: Area?,
    val isnis: List<String>,
    val lifeSpan: LifeSpan?,
    val tags: List<Tag>,
    val relations: List<Relation>?,
    val wikiDescription: String? = null,
    val thumbnailImageUrl: String? = null,
    val isSaved: Boolean = false,
    val releaseGroups: List<ReleaseGroup>? = null,
) {
    @kotlinx.serialization.Serializable
    data class Area(
        val id: String,
        val type: String,
        val name: String,
    )

    @kotlinx.serialization.Serializable
    data class LifeSpan(
        val begin: String?,
        val ended: String?
    )

    @kotlinx.serialization.Serializable
    data class Tag(
        val count: Int,
        val name: String
    )

    @kotlinx.serialization.Serializable
    data class Relation(
        val type: String?,
        val url: String?,
    ) {
        // Hardcoded strings coming from the MusicBrainz API, which is a small violation of the
        // principle of separation of concerns.
        // A better approach would be to provide them as an enum/sealed class in the domain layer.
        val isTypeWikipedia: Boolean = type == "wikipedia"
        val isTypeWikidata: Boolean = type == "wikidata"

        val pageTitle: String?
            get() {
                return Uri.parse(url).lastPathSegment
            }
    }

    enum class Type(
        /**
         * String representation given by MusicBrainz.
         */
        val value: String
    ) {
        PERSON("Person"),
        GROUP("Group"),
        ORCHESTRA("Orchestra"),
        CHOIR("Choir"),
        CHARACTER("Character"),
        OTHER("Other"),
        UNKNOWN("Unknown");

        companion object {
            private val map by lazy { values().associateBy(Type::value) }
            fun fromValue(value: String?): Type = map[value] ?: UNKNOWN
        }
    }
}
