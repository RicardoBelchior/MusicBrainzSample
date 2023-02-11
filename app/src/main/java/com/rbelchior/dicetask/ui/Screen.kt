package com.rbelchior.dicetask.ui

import android.net.Uri

sealed class Screen(val route: String) {
    private object Routes {
        const val SEARCH = "app/artist/search"
        const val DETAIL_BASE = "app/artist/detail"
        const val DETAIL_ARG_ID = "id"
        const val DETAIL_ARG_NAME = "name"
        val DETAIL = buildDetailRoute("{$DETAIL_ARG_ID}", "{$DETAIL_ARG_NAME}")

        fun buildDetailRoute(id: String, name: String) = Uri.Builder()
            .path(DETAIL_BASE)
            .appendQueryParameter(DETAIL_ARG_ID, id)
            .appendQueryParameter(DETAIL_ARG_NAME, name)
            .toString()
    }

    object ArtistSearch : Screen(Routes.SEARCH)
    object ArtistDetail : Screen(Routes.DETAIL) {
        const val ARG_ARTIST_ID = Routes.DETAIL_ARG_ID
        const val ARG_ARTIST_NAME = Routes.DETAIL_ARG_NAME
        fun buildRoute(artistId: String, artistName: String) =
            Routes.buildDetailRoute(artistId, artistName)
    }
}
