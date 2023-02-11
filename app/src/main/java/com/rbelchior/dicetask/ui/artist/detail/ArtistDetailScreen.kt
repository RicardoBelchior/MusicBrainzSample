package com.rbelchior.dicetask.ui.artist.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rbelchior.dicetask.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    artistId: String,
    artistName: String,
    viewModel: ArtistDetailViewModel = koinViewModel { parametersOf(artistId) },
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        TopAppBar(
            title = { Text(artistName) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back icon"
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

            uiState.value.artist?.let {

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
                        placeholder = painterResource(id = R.drawable.ic_album),
                        fallback = painterResource(id = R.drawable.ic_album),
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

