package com.rbelchior.dicetask.ui.artist.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbelchior.dicetask.data.remote.util.NetworkException
import com.rbelchior.dicetask.data.repository.DiceRepository
import com.rbelchior.dicetask.domain.SearchArtistsResult
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchEvent
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchReducer
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchUiState
import com.rbelchior.dicetask.ui.util.paginator.DefaultPaginator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

class ArtistSearchViewModel(
    private val repository: DiceRepository,
    private val reducer: ArtistSearchReducer = ArtistSearchReducer()
) : ViewModel() {

    val uiState: StateFlow<ArtistSearchUiState> = reducer.state
        .onEach { logcat { "State: $it" } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistSearchUiState.DEFAULT
        )

    private val paginator = DefaultPaginator(
        initialKey = reducer.currentValue.offset,
        onLoadUpdated = {
            reducer.sendEvent(
                ArtistSearchEvent.LoadingStateUpdated(it)
            )
        },
        onRequest = { nextPage ->
            repository.searchArtist(reducer.currentValue.query, nextPage)
        },
        getNextKey = {
            reducer.currentValue.offset + SearchArtistsResult.PAGE_SIZE
        },
        onError = {
            if (it.isCancellationException()) {
                logcat { "Ignoring CancellationException, it's ok." }
                return@DefaultPaginator
            }
            reducer.sendEvent(ArtistSearchEvent.SearchRequestError(it))
        },
        onSuccess = { page, newKey ->
            reducer.sendEvent(
                ArtistSearchEvent.SearchRequestSuccess(
                    reducer.currentValue.artists + page.artists,
                    page.artists.isEmpty(), newKey
                )
            )
        }
    )

    private var searchJob: Job? = null

    fun loadNextItems() {
        withDelay {
            if (reducer.currentValue.query.isEmpty()) {
                return@withDelay
            }

            paginator.loadNextPage()
        }
    }

    fun onSearchInputChange(query: String) {
        logcat { "onSearchInputChange: $query" }
        reducer.sendEvent(
            ArtistSearchEvent.UserQueryUpdated(query)
        )

        if (query.isNotEmpty()) {
            paginator.reset()
            loadNextItems()
        }
    }

    fun onClearClicked() {
        logcat { "onClearClicked" }
        reducer.sendEvent(
            ArtistSearchEvent.UserQueryCleared
        )
    }

    private fun withDelay(action: suspend () -> Unit) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            action()
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchJob = null
    }

    private fun Throwable.isCancellationException() =
        this is NetworkException.UnknownError && throwable is CancellationException
}
