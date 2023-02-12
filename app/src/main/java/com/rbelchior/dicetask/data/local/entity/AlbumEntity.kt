package com.rbelchior.dicetask.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ArtistEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("artistId"),
        // When an Artist is deleted, using ON CASCADE ensures that SQLite will automatically
        // delete all corresponding albums
        onDelete = ForeignKey.CASCADE
    )]
)
data class AlbumEntity(
    @PrimaryKey val id: String,
    val title: String,
    val primaryType: String?,
    val secondaryTypes: List<String>?,
    val firstReleaseDate: String?,
    val thumbnailImageUrl: String?,

    val artistId: String
)
