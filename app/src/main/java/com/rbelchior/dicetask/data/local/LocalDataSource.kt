package com.rbelchior.dicetask.data.local

import com.rbelchior.dicetask.domain.Artist
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getSavedArtists(): Flow<List<Artist>>

    suspend fun getArtist(artistId: String): Artist?

    suspend fun saveArtist(artist: Artist)

    suspend fun removeArtist(artist: Artist)
}
