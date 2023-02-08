package com.rbelchior.dicetask.ui

sealed class Screen(val route: String) {
    object ArtistSearch : Screen("artist-search")
    object ArtistDetail : Screen("artist-detail")
}
