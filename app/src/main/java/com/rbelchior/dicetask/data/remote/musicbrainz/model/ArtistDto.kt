package com.rbelchior.dicetask.data.remote.musicbrainz.model

import android.net.Uri
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: String,
    val score: Int?,
    val name: String,
    val type: String?,
    val area: AreaDto?,
    @SerialName("begin-area")
    val beginArea: AreaDto?,
    val isnis: List<String>?,
    @SerialName("life-span")
    val lifeSpan: LifeSpanDto?,
    val tags: List<TagDto>?,
    val relations: List<RelationDto>?
) {

    @Serializable
    data class AreaDto(
        val id: String?,
        val type: String?,
        val name: String?,
    )

    @Serializable
    data class LifeSpanDto(
        val begin: String?,
        val ended: String?
    )

    @Serializable
    data class TagDto(
        val count: Int?,
        val name: String?
    )

    @Serializable
    data class RelationDto(
        val type: String?,
        val url: UrlDto?,
    ) {
        val isTypeWikipedia: Boolean = type == "wikipedia"
        val isTypeWikidata: Boolean = type == "wikidata"

        val pageTitle: String?
            get() {
                return Uri.parse(url?.resource).lastPathSegment
            }
    }

    @Serializable
    data class UrlDto(
        val id: String?,
        val resource: String?
    )
}
