package com.rbelchior.dicetask.di

import com.rbelchior.dicetask.data.remote.musicbrainz.MusicBrainzRemoteDataSource
import com.rbelchior.dicetask.data.remote.musicbrainz.MusicBrainzRemoteDataSourceImpl
import com.rbelchior.dicetask.data.remote.util.createHttpClient
import com.rbelchior.dicetask.data.remote.wiki.WikiRemoteDataSource
import com.rbelchior.dicetask.data.remote.wiki.WikiRemoteDataSourceImpl
import io.ktor.client.engine.android.*
import org.koin.dsl.module

internal val networkModule = module {
    single { createHttpClient(engine = Android.create()) }

    single<MusicBrainzRemoteDataSource> { MusicBrainzRemoteDataSourceImpl(get()) }
    single<WikiRemoteDataSource> { WikiRemoteDataSourceImpl(get()) }
}
