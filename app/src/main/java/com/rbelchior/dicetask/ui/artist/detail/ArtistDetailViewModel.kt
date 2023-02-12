package com.rbelchior.dicetask.ui.artist.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbelchior.dicetask.data.repository.DiceRepository
import com.rbelchior.dicetask.domain.Artist
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArtistDetailViewModel(
    private val artistId: String,
    private val repository: DiceRepository,
) : ViewModel() {

    data class UiState(
        val artist: Artist? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState()
        )

    init {
        repository.getArtistDetails(artistId)
            .mapNotNull { it.getOrNull() }
            .onEach { artist -> _uiState.update { it.copy(artist = artist) } }
            .launchIn(viewModelScope)

    }

    fun toggleSavedArtist(artist: Artist) {
        viewModelScope.launch {
            if (artist.isSaved) {
                repository.removeArtist(artist)
            } else {
                repository.saveArtist(artist)
            }
        }
        _uiState.update {
            it.copy(
                artist = artist.copy(isSaved = !artist.isSaved)
            )
        }
    }
}