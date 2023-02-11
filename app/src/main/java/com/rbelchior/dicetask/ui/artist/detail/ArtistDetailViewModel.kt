package com.rbelchior.dicetask.ui.artist.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbelchior.dicetask.data.repository.DiceRepository
import com.rbelchior.dicetask.domain.Artist
import kotlinx.coroutines.flow.*

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


        repository.getArtistDetailsFlow(artistId)
            .mapNotNull { it.getOrNull() }
            .onEach { artist -> _uiState.update { it.copy(artist = artist) } }
            .launchIn(viewModelScope)

//        viewModelScope.launch {
//            repository.getArtistDetails(artistId)
//                .getOrNull()?.let { artist ->
//                    _uiState.update { it.copy(artist = artist) }
//                }
//        }
    }
}