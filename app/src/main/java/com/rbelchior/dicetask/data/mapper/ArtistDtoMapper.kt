package com.rbelchior.dicetask.data.mapper

import com.rbelchior.dicetask.data.remote.musicbrainz.model.ArtistDto
import com.rbelchior.dicetask.data.remote.musicbrainz.model.SearchArtistsResultDto
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.SearchArtistsResult

fun SearchArtistsResultDto.toDomain(): SearchArtistsResult {
    return SearchArtistsResult(
        count, offset, artists?.map { it.toDomain() } ?: emptyList()
    )
}

fun ArtistDto.toDomain(): Artist {
    return Artist(
        id,
        name,
        Artist.Type.fromValue(type),
        area?.toDomain(),
        beginArea?.toDomain(),
        isnis ?: emptyList(),
        lifeSpan?.let {
            Artist.LifeSpan(it.begin, it.ended)
        },
        tags
            ?.map { Artist.Tag(it.count, it.name) }
            ?.sortedByDescending { it.count }?.take(5) // Take only the 5 most relevant tags
            ?: emptyList(),
        null,
        null,
        null,
        false
    )
}

private fun ArtistDto.AreaDto.toDomain(): Artist.Area {
    return Artist.Area(id!!, type ?: "", name ?: "")
}
