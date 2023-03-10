package com.rbelchior.dicetask.data.remote.musicbrainz.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LookupAlbumsResultDto(
    @SerialName("release-group-offset")
    val releaseGroupOffset: Int,
    @SerialName("release-group-count")
    val releaseGroupCount: Int,
    @SerialName("release-groups")
    val releaseGroups: List<ReleaseGroupDto>?
) {

    @Serializable
    data class ReleaseGroupDto(
        val id: String,
        val title: String,
        @SerialName("primary-type")
        val primaryType: String?,
        @SerialName("secondary-types")
        val secondaryTypes: List<String>?,
        @SerialName("first-release-date")
        val firstReleaseDate: String?
    )
}