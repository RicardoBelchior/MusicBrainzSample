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
        val artist: Artist? = null,
        val throwable: Throwable? = null
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
            .onEach { result ->
                if (result.isSuccess) {
                    _uiState.update { it.copy(
                        artist = result.getOrThrow(),
                        throwable = result.exceptionOrNull()
                    ) }
                } else {
                    _uiState.update { it.copy(throwable = result.exceptionOrNull()) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleSavedArtist(artist: Artist) {
        viewModelScope.launch {
            repository.toggleArtistSaved(artist)
        }
        _uiState.update {
            it.copy(
                artist = artist.copy(isSaved = !artist.isSaved)
            )
        }
    }
}