package com.rbelchior.dicetask.data.remote.musicbrainz.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchArtistsResultDto(
    val created: String,
    val count: Int,
    val offset: Int,
    val artists: List<ArtistDto>?
)
