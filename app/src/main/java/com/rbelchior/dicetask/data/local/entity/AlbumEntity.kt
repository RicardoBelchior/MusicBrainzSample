package com.rbelchior.dicetask.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ArtistEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("artistId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class AlbumEntity(
    @PrimaryKey val id: String,
    val title: String,
    val primaryType: String?,
    val secondaryType: String?,
    val firstReleaseDate: String?,
    val thumbnailImageUrl: String?,

    val artistId: String
)
