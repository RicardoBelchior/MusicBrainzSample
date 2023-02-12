package com.rbelchior.dicetask.ui.artist.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbelchior.dicetask.data.repository.DiceRepository
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.FriendlyException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArtistDetailViewModel(
    artistId: String,
    private val repository: DiceRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistDetailUiState()
        )

    init {
        repository.getArtistDetails(artistId)
            .filterNot { it.isFailure && it.exceptionOrNull() is FriendlyException }
            .onEach { result ->
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            artist = result.getOrThrow(),
                            throwable = result.exceptionOrNull()
                        )
                    }
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