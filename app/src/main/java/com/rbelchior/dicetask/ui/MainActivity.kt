package com.rbelchior.dicetask.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rbelchior.dicetask.ui.artist.detail.ArtistDetailScreen
import com.rbelchior.dicetask.ui.artist.search.ArtistSearchScreen
import com.rbelchior.dicetask.ui.theme.DiceTaskTheme
import org.koin.compose.KoinContext
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            KoinContext {
                DiceTaskTheme {
                    Scaffold(
                        topBar = {}, // This could be used for the toolbar
                        bottomBar = {}, // Or the bottom navigation
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { contentPadding ->
                        NavigationComponent(
                            navController,
                            snackbarHostState,
                            Modifier.padding(contentPadding)
                        )
                    }
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
        composable(Screen.ArtistSearch.route) {
            ArtistSearchScreen(navController, snackbarHostState)
        }
        composable(
            Screen.ArtistDetail.route,
            arguments = listOf(
                navArgument(Screen.ArtistDetail.ARG_ARTIST_ID) { type = NavType.StringType },
                navArgument(Screen.ArtistDetail.ARG_ARTIST_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val navArgs = backStackEntry.arguments!!
            val artistId = navArgs.getString(Screen.ArtistDetail.ARG_ARTIST_ID)!!
            val artistName = navArgs.getString(Screen.ArtistDetail.ARG_ARTIST_NAME)!!
            ArtistDetailScreen(navController, snackbarHostState, artistId, artistName)
        }
    }
}
