package com.rbelchior.dicetask.ui.artist.search

import com.rbelchior.dicetask.data.repository.DiceRepository
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.SearchArtistsResult
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchIntent
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchUiState
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import logcat.LogcatLogger
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistSearchViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var repository: DiceRepository
    private lateinit var viewModel: ArtistSearchViewModel

    private val artists: List<Artist> = ArtistFactory.artists.toList()

    @Before
    fun setUp() {
        LogcatLogger.install(logcat.PrintLogger)

        repository = mockk()
        coEvery { repository.searchArtist(any(), any()) } coAnswers {
            delay(1000)
            Result.success(
                SearchArtistsResult(artists.size, 0, artists)
            )
        }
        viewModel = ArtistSearchViewModel(repository)
    }

    // Ensuring the initial state is correct
    @Test
    fun initial_state_should_be_default() {
        viewModel.uiState.value shouldBe ArtistSearchUiState.DEFAULT
    }

    // Ensuring the repository is invoked in the correct way
    @Test
    fun when_query_updated_should_trigger_request() = runTest {
        onIntent(ArtistSearchIntent.OnQueryUpdated("coldplay"))

        coVerify { repository.searchArtist("coldplay", 0) }
    }

    // Testing every UI State emitted, in detail
    @Test
    fun when_query_updated_should_return_correct_ui_states() = runTest {

        uiStatesWhen(ArtistSearchIntent.OnQueryUpdated("coldplay")).let { uiStates ->
            uiStates.size shouldBe 4

            uiStates[0].let {
                it.query shouldBe ""
                it.isLoading shouldBe false
            }

            uiStates[1].let {
                it.query shouldBe "coldplay"
                it.isLoading shouldBe false
            }

            uiStates[2].let {
                it.query shouldBe "coldplay"
                it.isLoading shouldBe true
            }

            uiStates[3].let {
                it.query shouldBe "coldplay"
                it.isLoading shouldBe false
                it.searchResults shouldBe artists
            }
        }
    }

    // Ensure the UI behaves correctly when the network request fails
    @Test
    fun when_query_updated_but_request_fails_should_return_correct_ui_states() = runTest {
        //TODO...
    }

    // Test the remaining user intents
    @Test
    fun when_clear_query_should_return_correct_ui_states() = runTest {
        //TODO...
    }

    @Test
    fun when_load_more_should_return_correct_ui_states() = runTest {
        //TODO...
    }

    @Test
    fun when_artist_clicked_should_return_correct_ui_states() = runTest {
        //TODO...
    }

    // Process the given user intent and block the coroutine until all UI states are handled.
    private fun TestScope.uiStatesWhen(
        intent: ArtistSearchIntent
    ): List<ArtistSearchUiState> {
        val values = mutableListOf<ArtistSearchUiState>()
        val collectJob = launch(mainCoroutineRule.dispatcher) {
            viewModel.uiState.toList(values)
        }
        this@ArtistSearchViewModelTest.onIntent(intent)
        collectJob.cancel()

        return values
    }

    private fun onIntent(intent: ArtistSearchIntent) {
        viewModel.onIntent(intent)

        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

    }
}
