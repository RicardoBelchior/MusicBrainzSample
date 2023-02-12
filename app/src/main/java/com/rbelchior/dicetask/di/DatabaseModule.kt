package com.rbelchior.dicetask.di

import androidx.room.Room
import com.rbelchior.dicetask.data.local.ArtistsDatabase
import com.rbelchior.dicetask.data.local.LocalDataSource
import com.rbelchior.dicetask.data.local.LocalDataSourceImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

internal val databaseModule = module {
    single {
        Room.databaseBuilder(androidApplication(), ArtistsDatabase::class.java, "artists-db")
            .build()
    }
    single { get<ArtistsDatabase>().artistDao() }
    single { get<ArtistsDatabase>().albumDao() }
    single<LocalDataSource> { LocalDataSourceImpl(get()) }
}
