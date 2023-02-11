package com.rbelchior.dicetask.data.mapper

import com.rbelchior.dicetask.data.local.entity.AlbumEntity
import com.rbelchior.dicetask.domain.ReleaseGroup

fun AlbumEntity.toDomain(): ReleaseGroup {
    return ReleaseGroup(
        id,
        title,
        primaryType,
        secondaryType,
        firstReleaseDate,
        thumbnailImageUrl
    )
}

fun ReleaseGroup.toEntity(artistId: String): AlbumEntity {
    return AlbumEntity(
        id,
        title,
        primaryType,
        secondaryType,
        firstReleaseDate,
        thumbnailImageUrl,
        artistId
    )
}