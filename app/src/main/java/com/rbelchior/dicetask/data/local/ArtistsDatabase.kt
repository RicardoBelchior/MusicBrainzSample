package com.rbelchior.dicetask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rbelchior.dicetask.data.local.converter.*
import com.rbelchior.dicetask.data.local.dao.AlbumDao
import com.rbelchior.dicetask.data.local.dao.ArtistDao
import com.rbelchior.dicetask.data.local.entity.AlbumEntity
import com.rbelchior.dicetask.data.local.entity.ArtistEntity

@Database(
    version = 1,
    entities = [
        ArtistEntity::class,
        AlbumEntity::class
    ],
    exportSchema = true,
)
@TypeConverters(
    // Type converters allow for simple transformations of data classes into strings
    AreaConverter::class,
    LifeSpanConverter::class,
    TagsConverter::class,
    RelationsConverter::class,
    IsnisConverter::class
)
abstract class ArtistsDatabase : RoomDatabase() {
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
}
