package com.rbelchior.dicetask.ui.artist.detail.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.rbelchior.dicetask.ui.artist.search.ArtistFactory
import com.rbelchior.dicetask.ui.artist.search.ArtistSearchScreen
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchUiState
import com.rbelchior.dicetask.ui.theme.DiceTaskTheme
import org.junit.Rule
import org.junit.Test

/**
 * Simple Compose UI test to demonstrate usage of the Compose testing API.
 * With more time, I would refactor this class to use the Testing Robots pattern.
 */
class ArtistSearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun when_launch_should_display_initial_state() {
        startScreen(ArtistSearchUiState.DEFAULT)

        composeTestRule.onNodeWithText("Hello Dice")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Search artists input", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals("")

        composeTestRule
            .onNodeWithContentDescription("Search artists placeholder", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextEquals("Search artists…")

        composeTestRule.onNodeWithContentDescription("Artists list")
            .onChildren()
            .assertCountEquals(0)
    }

    @Test
    fun when_clear_clicked_should_clear_input() {
        startScreen(ArtistSearchUiState.DEFAULT)

        composeTestRule
            .onNodeWithContentDescription("Search artists input", useUnmergedTree = true)
            .performTextInput("some query")

        composeTestRule
            .onNodeWithContentDescription("Clear icon")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Search artists input", useUnmergedTree = true)
            .assertTextEquals("")

        composeTestRule.onNodeWithText("Hello Dice").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Search artists input").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Artists list")
            .onChildren()
            .assertCountEquals(0)
    }

    @Test
    fun when_query_performed_display_artists() {
        val artists = ArtistFactory.artists.toList()

        startScreen(
            ArtistSearchUiState(
                "Coldplay", false, artists,
                emptyList(), null, false, 0
            )
        )

        composeTestRule
            .onAllNodesWithTag("Artist item")
            .assertCountEquals(artists.size)
    }

    private fun startScreen(state: ArtistSearchUiState) {
        composeTestRule.setContent {
            DiceTaskTheme {
                ArtistSearchScreen(
                    state, {}, {}, {}
                ) {}
            }
        }
    }
}