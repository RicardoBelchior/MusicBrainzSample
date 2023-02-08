package com.rbelchior.dicetask.ui.artist.search.mvi

sealed interface ArtistSearchIntent {

    data class Init(
        val query: String
    ) : ArtistSearchIntent

    data class UpdateQuery(val query: String) : ArtistSearchIntent

    object ClearQuery : ArtistSearchIntent

    data class SelectArtist(val artistId: String) : ArtistSearchIntent
}
