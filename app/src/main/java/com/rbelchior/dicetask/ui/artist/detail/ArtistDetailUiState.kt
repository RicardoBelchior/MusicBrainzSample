package com.rbelchior.dicetask.ui.artist.detail

import com.rbelchior.dicetask.domain.Artist

data class ArtistDetailUiState(
    val artist: Artist? = null,
    val throwable: Throwable? = null
)
