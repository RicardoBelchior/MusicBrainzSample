package com.rbelchior.dicetask.ui.artist.search

import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchUiState

/**
 * Factory for Compose Preview purposes.
 * In a production app, this could be left out of the code.
 */
internal object ArtistFactory {

    val artists = sequenceOf(
        Artist(
            "id-1", "Coldplay", Artist.Type.GROUP,
            Artist.Area("pt", "country", "Portugal"),
            Artist.Area("area", "city", "London"),
            emptyList(),
            Artist.LifeSpan("1996-12", null), emptyList(), emptyList(), null
        ),
        Artist(
            "id-2", "P!nk", Artist.Type.PERSON,
            null,
            null,
            emptyList(),
            Artist.LifeSpan("1996-12", null), emptyList(), emptyList(), null
        ),
        Artist(
            "id-3", "Fancy Orchestra", Artist.Type.ORCHESTRA,
            Artist.Area("uk", "country", "UK"),
            null,
            emptyList(),
            null, emptyList(), emptyList(), null
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
