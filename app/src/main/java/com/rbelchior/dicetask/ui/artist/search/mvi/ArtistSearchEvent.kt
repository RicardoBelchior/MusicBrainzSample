package com.rbelchior.dicetask.ui.artist.search.mvi

import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.util.UiEvent

sealed interface ArtistSearchEvent : UiEvent {

    /**
     * Triggered whenever the user types a new character in the TextField
     */
    data class UserQueryUpdated(val query: String) : ArtistSearchEvent

    /**
     * The user clicked the 'x' IconButton on the TextField
     */
    object UserQueryCleared : ArtistSearchEvent

    /**
     * The loading state has been updated
     */
    data class LoadingStateUpdated(val isLoading: Boolean) : ArtistSearchEvent

    /**
     * A successful response from the search request was obtained
     */
    data class SearchRequestSuccess(
        val artists: List<Artist>,
        val endReached: Boolean,
        val offset: Int
    ) : ArtistSearchEvent

    /**
     * An error response was returned from the search request
     */
    data class SearchRequestError(val throwable: Throwable) : ArtistSearchEvent

    /**
     * Triggered when the list of saved artists has changed.
     */
    data class SavedArtistsUpdated(val savedArtists: List<Artist>) : ArtistSearchEvent

}