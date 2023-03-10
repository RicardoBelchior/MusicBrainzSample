package com.rbelchior.dicetask.ui.artist.search.mvi

import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.util.UiState

data class ArtistSearchUiState(
    val query: String,
    val isLoading: Boolean,
    val searchResults: List<Artist>,
    val savedArtists: List<Artist>,
    val throwable: Throwable?,
    val endReached: Boolean,
    val offset: Int
) : UiState {

    val shouldAnimateScrollToBottom = isLoading && searchResults.isNotEmpty()

    val shouldDisplaySavedArtists = savedArtists.isNotEmpty() && !isLoading && query.isEmpty()

    val shouldLoadMore = !endReached && !isLoading

    /**
     * Using a "filtered" list to take advantage of the item animations provided by LazyRow.
     */
    val filteredSavedArtists = if (shouldDisplaySavedArtists) savedArtists else emptyList()

    companion object {
        val DEFAULT = ArtistSearchUiState(
            query = "", isLoading = false,
            searchResults = emptyList(),
            savedArtists = emptyList(),
            throwable = null,
            endReached = false,
            offset = 0
        )
    }

    override fun toString(): String {
        return "ArtistSearchUiState(query='$query', isLoading=$isLoading, artists.size=${searchResults.size}, throwable=$throwable, endReached=$endReached, offset=$offset)"
    }
}
