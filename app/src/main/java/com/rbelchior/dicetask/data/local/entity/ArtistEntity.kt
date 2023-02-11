package com.rbelchior.dicetask.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rbelchior.dicetask.domain.Artist

@Entity
data class ArtistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val area: Artist.Area?,
    val beginArea: Artist.Area?,
    val isnis: List<String>,
    val lifeSpan: Artist.LifeSpan?,
    val tags: List<Artist.Tag>,
    val wikiDescription: String?,
    val thumbnailImageUrl: String?,
)
