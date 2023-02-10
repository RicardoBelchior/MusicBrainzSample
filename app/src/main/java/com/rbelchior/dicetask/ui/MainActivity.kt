package com.rbelchior.dicetask.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rbelchior.dicetask.ui.artist.search.ArtistSearchScreen
import com.rbelchior.dicetask.ui.theme.DiceTaskTheme
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            DiceTaskTheme {
                Scaffold(
                    topBar = {}, // This could be used for the toolbar
                    bottomBar = {}, // Or the bottom navigation
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { contentPadding ->
                    NavigationComponent(navController, snackbarHostState, Modifier.padding(contentPadding))
                }
            }
        }
    }
}

@Composable
fun NavigationComponent(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ArtistSearch.route,
        modifier = modifier
    ) {
        composable(Screen.ArtistSearch.route) { ArtistSearchScreen(navController, snackbarHostState) }
        composable(Screen.ArtistDetail.route) { Text("DETAIL") }
    }
}
