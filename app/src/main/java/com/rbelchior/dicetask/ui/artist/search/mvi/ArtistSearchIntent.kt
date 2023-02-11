package com.rbelchior.dicetask.ui.artist.search.mvi

sealed interface ArtistSearchIntent {

    data class OnQueryUpdated(val query: String) : ArtistSearchIntent

    object OnClearQueryClicked : ArtistSearchIntent

    object OnLoadMoreItems : ArtistSearchIntent
}
