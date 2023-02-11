package com.rbelchior.dicetask.data.mapper

import com.rbelchior.dicetask.data.local.entity.ArtistEntity
import com.rbelchior.dicetask.data.local.entity.ArtistWithAlbums
import com.rbelchior.dicetask.domain.Artist

fun ArtistEntity.toDomain(): Artist {
    return Artist(
        id,
        name,
        Artist.Type.fromValue(type),
        area,
        beginArea,
        isnis,
        lifeSpan,
        tags,
        wikiDescription,
        thumbnailImageUrl,
        emptyList()
    )
}

fun ArtistWithAlbums.toDomain(): Artist {
    return artist
        .toDomain()
        .copy(releaseGroups = albums.map { album -> album.toDomain() })
}

fun Artist.toEntity(): ArtistEntity {
    return ArtistEntity(
        id,
        name,
        type.value,
        area,
        beginArea,
        isnis,
        lifeSpan,
        tags,
        wikiDescription,
        thumbnailImageUrl
    )
}