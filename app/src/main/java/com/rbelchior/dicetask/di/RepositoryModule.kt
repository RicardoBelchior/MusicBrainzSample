package com.rbelchior.dicetask.di

import com.rbelchior.dicetask.data.repository.DiceRepository
import org.koin.dsl.module

internal val repositoryModule = module {

    single { DiceRepository(get(), get(), get()) }
}
