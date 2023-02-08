package com.rbelchior.dicetask.ui.artist.search.mvi

import com.rbelchior.dicetask.ui.util.Reducer
import logcat.logcat

class ArtistSearchReducer :
    Reducer<ArtistSearchUiState, ArtistSearchEvent>(ArtistSearchUiState.DEFAULT) {

    override fun reduce(oldState: ArtistSearchUiState, event: ArtistSearchEvent) {
        val newState = when (event) {
            is ArtistSearchEvent.UserQueryUpdated -> oldState.copy(
                query = event.query, artists = emptyList(),
                endReached = false, offset = 0
            )
            ArtistSearchEvent.UserQueryCleared -> oldState.copy(
                query = "", artists = emptyList(),
                endReached = false, offset = 0
            )
            is ArtistSearchEvent.LoadingStateUpdated -> oldState.copy(
                isLoading = event.isLoading
            )
            is ArtistSearchEvent.SearchRequestSuccess -> oldState.copy(
                artists = event.artists,
                endReached = event.endReached,
                offset = event.offset
            )
            is ArtistSearchEvent.SearchRequestError -> oldState.copy(
                throwable = event.throwable
            )
        }
        setState(newState)
    }

}

