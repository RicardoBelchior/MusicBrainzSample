package com.rbelchior.dicetask.ui.artist.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.ReleaseGroup
import com.rbelchior.dicetask.ui.components.AnimatedIconToggleButton
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ArtistDetailScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    artistId: String,
    artistName: String,
    viewModel: ArtistDetailViewModel = koinViewModel { parametersOf(artistId) },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val albumDialogOpened: MutableState<ReleaseGroup?> = remember { mutableStateOf(null) }

    // In case an error exists, display it with a snackbar
    uiState.value.throwable?.let {
        LaunchedEffect(snackbarHostState, it) {
            snackbarHostState.showSnackbar(
                message = "Error: ${it.message ?: "unknown"}"
            )
        }
    }

    // Display album dialog, when needed
    albumDialogOpened.value?.let {
        AlbumDetailsDialog(releaseGroup = it) { albumDialogOpened.value = null }
    }

    ArtistDetailScreen(
        artistName,
        uiState.value,
        { navController.popBackStack() },
        {
            viewModel.toggleSavedArtist(it)
            scope.launch {
                val messageSuffix = if (it.isSaved) " removed." else " saved."
                snackbarHostState.showSnackbar(
                    message = it.name + messageSuffix,
                    duration = SnackbarDuration.Short
                )
            }
        },
        { albumDialogOpened.value = it }
    )
}

@Composable
fun ArtistDetailScreen(
    artistName: String,
    uiState: ArtistDetailUiState,
    onBackButtonClicked: () -> Unit,
    onSaveArtistClicked: (Artist) -> Unit,
    onAlbumClicked: (ReleaseGroup) -> Unit
) {
    Column {
        ArtistDetailTopBar(artistName, uiState, onBackButtonClicked, onSaveArtistClicked)

        uiState.artist?.let {
            ArtistDetailMainContent(it, onAlbumClicked)
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ArtistDetailTopBar(
    artistName: String,
    uiState: ArtistDetailUiState,
    onBackButtonClicked: () -> Unit,
    onSaveArtistClicked: (Artist) -> Unit
) {
    TopAppBar(
        title = { Text(artistName) },
        navigationIcon = {
            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back icon"
                )
            }
        },
        actions = {
            if (uiState.artist != null) {

                AnimatedIconToggleButton(
                    isChecked = uiState.artist.isSaved,
                    onCheckedChange = {
                        onSaveArtistClicked(uiState.artist)
                    }
                )
            }
        }
    )
}

