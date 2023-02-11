package com.rbelchior.dicetask.ui.artist.search

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun ArtistSearchScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: ArtistSearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArtistSearchScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onValueChange = {
            viewModel.onIntent(ArtistSearchIntent.OnQueryUpdated(it))
        },
        onClearClicked = {
            viewModel.onIntent(ArtistSearchIntent.OnClearQueryClicked)
        },
        onLoadMore = {
            viewModel.onIntent(ArtistSearchIntent.OnLoadMoreItems)
        },
        onArtistClicked = {
            navController.navigate(Screen.ArtistDetail.buildRoute(it.id, it.name))
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ArtistSearchScreen(
    uiState: ArtistSearchUiState,
    snackbarHostState: SnackbarHostState,
    onValueChange: (String) -> Unit,
    onClearClicked: () -> Unit,
    onLoadMore: () -> Unit,
    onArtistClicked: (artist: Artist) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // In case it exists, display error in the snackbar
    uiState.throwable?.let {
        LaunchedEffect(snackbarHostState, uiState.throwable) {
            keyboardController?.hide()
            snackbarHostState.showSnackbar(
                message = "Error: ${it.message ?: "unknown"}"
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Hello Dice",
            style = MaterialTheme.typography.headlineLarge
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
        uiState, SnackbarHostState(), {}, {}, {}, {}
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
                text = "Search artists...",
                modifier = Modifier.semantics { contentDescription = "Search artists placeholder" })
        },
        trailingIcon = {
            IconButton(
                onClick = onClearClicked,
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Clear icon"
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
    if (uiState.isLoading && uiState.artists.isNotEmpty()) {
        val itemSize = 50.dp // Approx. assuming an item height of 50dp.
        val itemSizePx = with(LocalDensity.current) { itemSize.toPx() }
        LaunchedEffect(uiState.artists.size) {
            listState.animateScrollBy(
                value = itemSizePx * uiState.artists.size + 1,
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
        itemsIndexed(uiState.artists) { i, artist ->
            if (i >= uiState.artists.size - 1 && !uiState.endReached && !uiState.isLoading) {
                onLoadMore()
            }
            ArtistItem(artist, onArtistClicked)

            // Display divider between each item
            if (i < uiState.artists.lastIndex) {
                Divider()
            }
        }
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
fun ArtistItem(artist: Artist, onClick: (artist: Artist) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick(artist) }
            .semantics { testTag = "Artist item" }
    ) {
        Row {
            ArtistIcon(artist.type, modifier = Modifier.padding(end = 8.dp))
            Text(text = artist.name)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = artist.buildLabel(),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistItemPreview(
    @PreviewParameter(ArtistPreviewParameterProvider::class) artist: Artist
) {
    ArtistItem(artist) {}
}

private fun Artist.buildLabel() = buildString {
    append(type.value)
    area?.name?.let {
        append(" from $it")
    }
    lifeSpan?.begin?.let {
        area?.name?.let {
            append(",")
        }
        if (type == Artist.Type.PERSON) {
            append(" born in: $it")
        } else {
            append(" founded in: $it")
        }
    }
}

@Composable
fun ArtistIcon(artistType: Artist.Type, modifier: Modifier) {
    when (artistType) {
        Artist.Type.PERSON -> Icon(
            Icons.Outlined.Person,
            stringResource(id = R.string.content_description_person),
            modifier,
        )
        Artist.Type.GROUP -> Icon(
            painterResource(id = R.drawable.ic_groups),
            stringResource(id = R.string.content_description_group),
            modifier,
        )
        Artist.Type.ORCHESTRA -> Icon(
            painterResource(id = R.drawable.ic_piano),
            stringResource(id = R.string.content_description_orchestra),
            modifier,
        )
        Artist.Type.CHOIR -> Icon(
            painterResource(id = R.drawable.ic_music_note),
            stringResource(id = R.string.content_description_choir),
            modifier,
        )
        Artist.Type.CHARACTER -> Icon(
            Icons.Outlined.Face,
            stringResource(id = R.string.content_description_character),
            modifier,
        )
        Artist.Type.OTHER -> Icon(
            painterResource(id = R.drawable.ic_library_music),
            stringResource(id = R.string.content_description_other),
            modifier,
        )
        Artist.Type.UNKNOWN -> {}
    }
}
