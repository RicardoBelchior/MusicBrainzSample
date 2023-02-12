package com.rbelchior.dicetask.data.local

import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.ReleaseGroup
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getSavedArtists(): Flow<List<Artist>>

    suspend fun getArtist(artistId: String): Artist?

    suspend fun insertArtist(artist: Artist)

    suspend fun setArtistSaved(artistId: String, isSaved: Boolean)

    suspend fun setReleaseGroups(artistId: String, releaseGroups: List<ReleaseGroup>)

    suspend fun setWikiDetails(artistId: String, wikiDescription: String?, thumbnailUrl: String?)
}
