package com.rbelchior.dicetask.ui.artist.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    artistId: String,
    artistName: String
) {

    Column {
        TopAppBar(
            title = { Text(artistName) },
            navigationIcon = { }
        )
        Text("DETAIL: $artistId")
    }
}
