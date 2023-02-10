package com.rbelchior.dicetask.ui.artist.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchUiState

class ArtistSearchUiStatePreviewParameterProvider : PreviewParameterProvider<ArtistSearchUiState> {
    override val values = ArtistFactory.uiStates
}

class ArtistPreviewParameterProvider : PreviewParameterProvider<Artist> {
    override val values = ArtistFactory.artists
}
