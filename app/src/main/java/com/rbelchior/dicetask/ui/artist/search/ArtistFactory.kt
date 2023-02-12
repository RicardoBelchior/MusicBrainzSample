package com.rbelchior.dicetask.ui.artist.search

import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchUiState

/**
 * Factory for Compose Preview purposes.
 * In a production app, this could be left out of the code.
 */
internal object ArtistFactory {

    val artistWithFullData = Artist(
        "id-1", "Coldplay", Artist.Type.GROUP,
        Artist.Area("pt", "country", "Portugal"),
        Artist.Area("area", "city", "London"),
        listOf("isni1", "isni2"),
        Artist.LifeSpan("1996-12", null),
        listOf(Artist.Tag(10, "rock"), Artist.Tag(5, "pop")),
        listOf(Artist.Relation("wikipedia", "https://....")),
        "This is an amazing band from the UK."
    )

    val artistWithBasicData = Artist(
        "id-2", "P!nk", Artist.Type.PERSON,
        null,
        null,
        emptyList(),
        Artist.LifeSpan("1996-12", null), emptyList(), emptyList(),
        null, null, false, null
    )

    val artists = sequenceOf(
        artistWithFullData,
        artistWithBasicData,
        Artist(
            "id-3", "Fancy Orchestra", Artist.Type.ORCHESTRA,
            Artist.Area("uk", "country", "UK"),
            null,
            emptyList(),
            null, emptyList(), null, null
        )
    )

    val uiStates = sequenceOf(
        ArtistSearchUiState.DEFAULT,
        ArtistSearchUiState(
            "Coldplay", false, artists.toList(),
            emptyList(), null, false, 0
        )
    )
}
