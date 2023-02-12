package com.rbelchior.dicetask.domain

data class ReleaseGroup(
    val id: String,
    val title: String,
    val primaryType: String?,
    val secondaryTypes: List<String>?,
    val firstReleaseDate: String?,
    val thumbnailImageUrl: String?
) {

    val secondaryTypesJoined: String? by lazy {
        secondaryTypes?.joinToString(", ")?.ifEmpty { null }
    }
}
