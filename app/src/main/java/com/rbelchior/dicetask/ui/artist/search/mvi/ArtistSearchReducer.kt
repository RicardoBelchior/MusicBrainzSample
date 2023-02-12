package com.rbelchior.dicetask.ui.artist.search.mvi

import com.rbelchior.dicetask.ui.util.Reducer

class ArtistSearchReducer :
    Reducer<ArtistSearchUiState, ArtistSearchEvent>(ArtistSearchUiState.DEFAULT) {

    override fun reduce(oldState: ArtistSearchUiState, event: ArtistSearchEvent) {
        val newState = when (event) {
            is ArtistSearchEvent.UserQueryUpdated -> oldState.copy(
                query = event.query,
                searchResults = emptyList(),
                endReached = false, offset = 0,
                throwable = null
            )
            ArtistSearchEvent.UserQueryCleared -> oldState.copy(
                query = "", searchResults = emptyList(),
                endReached = false, offset = 0,
                throwable = null
            )
            is ArtistSearchEvent.LoadingStateUpdated -> oldState.copy(
                isLoading = event.isLoading,
            )
            is ArtistSearchEvent.SearchRequestSuccess -> oldState.copy(
                searchResults = event.artists,
                endReached = event.endReached,
                offset = event.offset,
                throwable = null
            )
            is ArtistSearchEvent.SearchRequestError -> oldState.copy(
                throwable = event.throwable
            )
            is ArtistSearchEvent.SavedArtistsUpdated -> oldState.copy(
                savedArtists = event.savedArtists
            )
        }
        setState(newState)
    }
}
