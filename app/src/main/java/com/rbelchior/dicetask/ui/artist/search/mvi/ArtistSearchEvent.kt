package com.rbelchior.dicetask.ui.artist.search.mvi

import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.util.UiEvent

sealed interface ArtistSearchEvent : UiEvent {

    data class UserQueryUpdated(val query: String) : ArtistSearchEvent

    object UserQueryCleared : ArtistSearchEvent

    data class LoadingStateUpdated(val isLoading: Boolean) : ArtistSearchEvent

    data class SearchRequestSuccess(
        val artists: List<Artist>,
        val endReached: Boolean,
        val offset: Int
    ) : ArtistSearchEvent

    data class SearchRequestError(val throwable: Throwable) : ArtistSearchEvent
}