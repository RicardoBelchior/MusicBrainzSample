package com.rbelchior.dicetask.di

import com.rbelchior.dicetask.ui.artist.search.ArtistSearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val viewModelsModule = module {
    viewModel { ArtistSearchViewModel(get()) }
}
