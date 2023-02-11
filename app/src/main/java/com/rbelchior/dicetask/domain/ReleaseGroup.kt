package com.rbelchior.dicetask.domain

data class ReleaseGroup(
    val id: String,
    val title: String,
    val primaryType: String?,
    val secondaryType: String?,
    val firstReleaseDate: String?,
    val thumbnailImageUrl: String?
)
