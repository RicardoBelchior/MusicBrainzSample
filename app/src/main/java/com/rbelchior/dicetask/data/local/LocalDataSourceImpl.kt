package com.rbelchior.dicetask.data.local

import androidx.room.withTransaction
import com.rbelchior.dicetask.data.local.dao.AlbumDao
import com.rbelchior.dicetask.data.local.dao.ArtistDao
import com.rbelchior.dicetask.data.mapper.toDomain
import com.rbelchior.dicetask.data.mapper.toEntity
import com.rbelchior.dicetask.domain.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataSourceImpl(
    private val artistsDatabase: ArtistsDatabase,
    private val artistDao: ArtistDao,
    private val albumDao: AlbumDao
) : LocalDataSource {

    override fun getSavedArtists(): Flow<List<Artist>> {
        return artistDao
            .getUsersWithPlaylists()
            .map { savedArtists -> savedArtists.map { it.toDomain() } }
    }

    override suspend fun getArtist(artistId: String): Artist? {
        return artistDao.getById(artistId)?.toDomain()
    }

    override suspend fun saveArtist(artist: Artist) {
        artistsDatabase.withTransaction {

            artistDao.insert(artist.toEntity())

            artist.releaseGroups
                ?.map { it.toEntity(artist.id) }
                ?.forEach { albumDao.insert(it) }
        }
    }

    override suspend fun removeArtist(artist: Artist) {
        artistDao.delete(artist.toEntity())
        // Note, using ON CASCADE to delete all albums of this artist
    }

}
