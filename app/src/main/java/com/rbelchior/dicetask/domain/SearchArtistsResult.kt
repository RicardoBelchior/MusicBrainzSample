package com.rbelchior.dicetask.domain

data class SearchArtistsResult(
    val count: Int,
    val offset: Int,
    val artists: List<Artist>
) {
    companion object {
        const val PAGE_SIZE = 15
    }
}
