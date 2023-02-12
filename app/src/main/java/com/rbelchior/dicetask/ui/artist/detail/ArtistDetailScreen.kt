package com.rbelchior.dicetask.ui.artist.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.domain.Artist
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

    // In case an error exists, display it with a snackbar
    uiState.value.throwable?.let {
        LaunchedEffect(snackbarHostState, it) {
            snackbarHostState.showSnackbar(
                message = "Error: ${it.message ?: "unknown"}"
            )
        }
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
                    duration = SnackbarDuration.Short)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistName: String,
    uiState: ArtistDetailViewModel.UiState,
    onBackButtonClicked: () -> Unit,
    onSaveArtistClicked: (Artist) -> Unit,
) {
    Column {
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
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            val placeholder = rememberAsyncImagePainter(R.drawable.ic_album)

            uiState.artist?.let {

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.thumbnailImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Album image",
                        contentScale = ContentScale.FillHeight,
                        alignment = Alignment.Center,
                        placeholder = placeholder,
                        fallback = placeholder,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .size(dimensionResource(id = R.dimen.artist_detail_image_size))
                    )
                }

                Text("TYPE: ${it.type}")
                Text("AREA: ${it.area}")
                Text("TAGS: ${it.tags.map { t -> t.name }}")
                Text("WIKI: ${it.wikiDescription}")
                Text("ALBUMS: ${it.releaseGroups?.map { releaseGroup -> releaseGroup.title }}")
            }
        }

    }
}

