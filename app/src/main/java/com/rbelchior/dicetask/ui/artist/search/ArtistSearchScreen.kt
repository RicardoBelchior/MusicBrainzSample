package com.rbelchior.dicetask.ui.artist.search

import androidx.compose.animation.core.tween
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.domain.Artist
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
            viewModel.onSearchInputChange(it)
        },
        onClearClicked = {
            viewModel.onClearClicked()
        },
        onLoadMore = { viewModel.loadNextItems() }
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
        ArtistsList(uiState, onLoadMore)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ArtistsTextInput(
    uiState: ArtistSearchUiState,
    onValueChange: (String) -> Unit,
    onClearClicked: () -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.query,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyLarge,
        placeholder = { Text("Search artists...") },
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
    onLoadMore: () -> Unit
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
        modifier = Modifier.fillMaxSize(),
        state = listState
    ) {
        itemsIndexed(uiState.artists) { i, artist ->
            if (i >= uiState.artists.size - 1 && !uiState.endReached && !uiState.isLoading) {
                onLoadMore()
            }
            ArtistItem(artist)

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
fun ArtistItem(artist: Artist) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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

@Preview
@Composable
fun ArtistItemPreview() {
    ArtistItem(
        artist = Artist(
            "id", "Coldplay", Artist.Type.GROUP,
            Artist.Area("uk", "country", "UK"),
            Artist.Area("area", "city", "London"),
            emptyList(),
            Artist.LifeSpan("1996-12", null), emptyList(), null
        )
    )
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
            stringResource(id = R.string.content_description_person),
            modifier,
        )
        Artist.Type.ORCHESTRA -> Icon(
            painterResource(id = R.drawable.ic_piano),
            stringResource(id = R.string.content_description_person),
            modifier,
        )
        Artist.Type.CHOIR -> Icon(
            painterResource(id = R.drawable.ic_music_note),
            stringResource(id = R.string.content_description_person),
            modifier,
        )
        Artist.Type.CHARACTER -> Icon(
            Icons.Outlined.Face,
            stringResource(id = R.string.content_description_person),
            modifier,
        )
        Artist.Type.OTHER -> Icon(
            painterResource(id = R.drawable.ic_library_music),
            stringResource(id = R.string.content_description_person),
            modifier,
        )
        Artist.Type.UNKNOWN -> {}
    }
}
