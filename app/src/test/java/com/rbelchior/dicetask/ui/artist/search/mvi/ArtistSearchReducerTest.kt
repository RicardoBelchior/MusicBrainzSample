package com.rbelchior.dicetask.ui.artist.search.mvi

import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.artist.search.ArtistFactory
import io.kotest.matchers.shouldBe
import org.junit.Test

class ArtistSearchReducerTest {

    private val reducer = ArtistSearchReducer()

    private val artistsList1: List<Artist> = listOf(ArtistFactory.artists.toList().first())

    @Test
    fun test_UserQueryUpdated() {
        reducer.reduce(
            ArtistSearchUiState("blabla", false, artistsList1, emptyList(), Throwable(), true, 9),
            ArtistSearchEvent.UserQueryUpdated("coldplay")
        )

        reducer.currentValue shouldBe ArtistSearchUiState(
            "coldplay", false, emptyList(), emptyList(), null, false, 0
        )
    }

    @Test
    fun test_UserQueryCleared() {
        //TODO...
    }

    @Test
    fun test_LoadingStateUpdated() {
        //TODO...
    }

    @Test
    fun test_SearchRequestSuccess() {
        //TODO...
    }

    @Test
    fun test_SearchRequestError() {
        //TODO...
    }
}
