package com.rbelchior.dicetask.ui.artist.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
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
        Text("ID: $artistId")
        Text("NAME: $artistName")
        uiState.value.artist?.let {
            Text("TYPE: ${it.type}")
            Text("AREA: ${it.area}")
            Text("TAGS: ${it.tags.map { t -> t.name }}")
            Text("WIKI: ${it.wikiDescription}")
        }

    }
}

