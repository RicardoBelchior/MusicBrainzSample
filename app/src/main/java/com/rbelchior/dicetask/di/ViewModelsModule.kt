package com.rbelchior.dicetask.di

import com.rbelchior.dicetask.ui.artist.detail.ArtistDetailViewModel
import com.rbelchior.dicetask.ui.artist.search.ArtistSearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val viewModelsModule = module {
    viewModel { ArtistSearchViewModel(get()) }
    viewModel { parametersHolder -> ArtistDetailViewModel(parametersHolder.get(), get()) }
}
