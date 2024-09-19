package com.rbelchior.dicetask.ui.artist.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.ui.Screen
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchIntent
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchUiState
import com.rbelchior.dicetask.ui.components.ArtistIcon
import com.rbelchior.dicetask.ui.components.ArtistLabel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArtistSearchScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: ArtistSearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current

    // In case an error exists, display it with a snackbar
    uiState.throwable?.let {
        LaunchedEffect(snackbarHostState, it) {
            keyboardController?.hide()
            snackbarHostState.showSnackbar(
                message = "Error: ${it.message ?: "unknown"}"
            )
        }
    }

    ArtistSearchScreen(
        uiState = uiState,
        onValueChange = {
            viewModel.onIntent(ArtistSearchIntent.OnQueryUpdated(it))
        },
        onClearClicked = {
            viewModel.onIntent(ArtistSearchIntent.OnClearQueryClicked)
        },
        onLoadMore = {
            viewModel.onIntent(ArtistSearchIntent.OnLoadMoreItems)
        }
    ) {
        navController.navigate(Screen.ArtistDetail.buildRoute(it.id, it.name))
    }
}

@Composable
fun ArtistSearchScreen(
    uiState: ArtistSearchUiState,
    onValueChange: (String) -> Unit,
    onClearClicked: () -> Unit,
    onLoadMore: () -> Unit,
    onArtistClicked: (artist: Artist) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.search_screen_title),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.size(24.dp))
        ArtistsTextInput(uiState, onValueChange, onClearClicked)
        ArtistsList(uiState, onLoadMore, onArtistClicked)
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistSearchScreenPreview(
    @PreviewParameter(ArtistSearchUiStatePreviewParameterProvider::class) uiState: ArtistSearchUiState
) {
    ArtistSearchScreen(
        uiState, {}, {}, {}
    ) {}
}

@Composable
private fun ArtistsTextInput(
    uiState: ArtistSearchUiState,
    onValueChange: (String) -> Unit,
    onClearClicked: () -> Unit
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Search artists input" },
        value = uiState.query,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_screen_input_placeholder),
                modifier = Modifier.semantics { contentDescription = "Search artists placeholder" })
        },
        trailingIcon = {
            IconButton(
                onClick = onClearClicked,
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(id = R.string.content_description_clear)
                )
            }
        }
    )
}

@Composable
fun ArtistsList(
    uiState: ArtistSearchUiState,
    onLoadMore: () -> Unit,
    onArtistClicked: (artist: Artist) -> Unit
) {
    val listState = rememberLazyListState()

    // When fetching the next page, automatically scroll to the last element to see the loading indicator.
    // Note: animateScrollToItem would also work, but this way we're doing a smoother animation.
    if (uiState.shouldAnimateScrollToBottom) {
        val itemSize = 50.dp // Approx. assuming an item height of 50dp.
        val itemSizePx = with(LocalDensity.current) { itemSize.toPx() }
        LaunchedEffect(uiState.searchResults.size) {
            listState.animateScrollBy(
                value = itemSizePx * uiState.searchResults.size + 1,
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Artists list" },
        state = listState
    ) {

        // Display search results
        itemsIndexed(uiState.searchResults) { i, artist ->
            if (i >= uiState.searchResults.size - 1 && uiState.shouldLoadMore) {
                onLoadMore()
            }
            ArtistItem(Modifier.animateItem(), artist, onArtistClicked)

            // Display divider between each item
            if (i < uiState.searchResults.lastIndex) {
                HorizontalDivider()
            }
        }

        // Display saved artists
        item {
            AnimatedVisibility(visible = uiState.shouldDisplaySavedArtists) {
                Text(
                    stringResource(id = R.string.search_screen_saved_artists),
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        itemsIndexed(uiState.filteredSavedArtists) { i, artist ->
            ArtistItem(Modifier.animateItem(), artist, onArtistClicked)

            // Display divider between each item
            if (i < uiState.savedArtists.lastIndex) {
                HorizontalDivider()
            }
        }

        // Loading indicator
        item {
            if (uiState.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ArtistItem(modifier: Modifier, artist: Artist, onClick: (artist: Artist) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(artist) }
            .semantics { testTag = "Artist item" }
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            ArtistIcon(artist.type, modifier = Modifier.padding(end = 8.dp))
            Text(text = artist.name)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            ArtistLabel(artist, MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistItemPreview(
    @PreviewParameter(ArtistPreviewParameterProvider::class) artist: Artist
) {
    ArtistItem(Modifier, artist) {}
}

