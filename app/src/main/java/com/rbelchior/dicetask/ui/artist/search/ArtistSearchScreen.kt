package com.rbelchior.dicetask.ui.artist.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.material.color.MaterialColors
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.ui.artist.search.mvi.ArtistSearchUiState
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
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


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(uiState.artists.size) { i ->
                val item = uiState.artists[i]
                if (i >= uiState.artists.size - 1 && !uiState.endReached && !uiState.isLoading) {
                    onLoadMore()
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = item.name)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("(${item.type})")
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
}
