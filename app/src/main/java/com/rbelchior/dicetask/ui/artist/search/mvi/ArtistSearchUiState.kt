package com.rbelchior.dicetask.ui.artist.search.mvi

import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.util.UiState

data class ArtistSearchUiState(
    val query: String,
    val isLoading: Boolean,
    val artists: List<Artist>,
    val savedArtists: List<Artist>,
    val throwable: Throwable?,
    val endReached: Boolean,
    val offset: Int
) : UiState {

    companion object {
        val DEFAULT = ArtistSearchUiState(
            query = "", isLoading = false,
            artists = emptyList(),
            throwable = null,
            endReached = false,
            offset = 0
        )
    }

    override fun toString(): String {
        return "ArtistSearchUiState(query='$query', isLoading=$isLoading, artists.size=${artists.size}, throwable=$throwable, endReached=$endReached, offset=$offset)"
    }
}
