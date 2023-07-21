package com.rbelchior.dicetask.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.rbelchior.dicetask.data.local.entity.ArtistEntity
import com.rbelchior.dicetask.data.local.entity.ArtistWithAlbums
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao : BaseDao<ArtistEntity> {

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE isSaved = 1 ORDER BY name")
    fun getSavedArtists(): Flow<List<ArtistWithAlbums>>

    @Transaction
    @Query("SELECT * FROM ArtistEntity WHERE id = :id")
    suspend fun getById(id: String): ArtistWithAlbums?

    @Query("UPDATE ArtistEntity SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedArtist(id: String, isSaved: Boolean)

    @Query(
        "UPDATE ArtistEntity SET wikiDescription = :wikiDescription, thumbnailImageUrl = :thumbnailImageUrl WHERE id = :id"
    )
    suspend fun updateWikiDetails(id: String, wikiDescription: String?, thumbnailImageUrl: String?)
}
