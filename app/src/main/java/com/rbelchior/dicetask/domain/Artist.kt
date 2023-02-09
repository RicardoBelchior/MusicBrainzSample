package com.rbelchior.dicetask.domain

data class Artist(
    val id: String,
    val name: String,
    val type: Type,
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
