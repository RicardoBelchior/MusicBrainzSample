package com.rbelchior.dicetask.data.local

import androidx.room.withTransaction
import com.rbelchior.dicetask.data.local.dao.AlbumDao
import com.rbelchior.dicetask.data.local.dao.ArtistDao
import com.rbelchior.dicetask.data.mapper.toDomain
import com.rbelchior.dicetask.data.mapper.toEntity
import com.rbelchior.dicetask.domain.Artist
import com.rbelchior.dicetask.domain.ReleaseGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataSourceImpl(
    private val artistsDatabase: ArtistsDatabase,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao
) : LocalDataSource {

    override fun getSavedArtists(): Flow<List<Artist>> {
        return artistDao
            .getSavedArtists()
            .map { savedArtists -> savedArtists.map { it.toDomain() } }
    }

    override suspend fun getArtist(artistId: String): Artist? {
        return artistDao.getById(artistId)?.toDomain()
    }

    override suspend fun insertArtist(artist: Artist) {
        artistsDatabase.withTransaction {
            val cachedArtist = getArtist(artist.id)

            artistDao.insert(artist.toEntity())

            // Insert operation will replace the existing album, so the following operations
            // ensure the previous data is not lost.
            cachedArtist?.let {
                setArtistSaved(it.id, it.isSaved)
                setWikiDetails(it.id, it.wikiDescription, it.thumbnailImageUrl)
                setReleaseGroups(it.id, it.releaseGroups ?: emptyList())
            }
        }
    }

    override suspend fun setArtistSaved(artistId: String, isSaved: Boolean) {
        artistDao.updateSavedArtist(artistId, isSaved)
    }

    override suspend fun setReleaseGroups(artistId: String, releaseGroups: List<ReleaseGroup>) {
        artistsDatabase.withTransaction {
            releaseGroups
                .map { it.toEntity(artistId) }
                .forEach { albumDao.insert(it) }
        }
    }

    override suspend fun setWikiDetails(
        artistId: String,
        wikiDescription: String?,
        thumbnailUrl: String?
    ) {
        artistDao.updateWikiDetails(artistId, wikiDescription, thumbnailUrl)
    }

}
