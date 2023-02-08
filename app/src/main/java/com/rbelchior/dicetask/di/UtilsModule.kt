package com.rbelchior.dicetask.di

import com.rbelchior.dicetask.util.JsonStringConverter
import org.koin.dsl.module

internal val utilsModule = module {
    factory { JsonStringConverter() }
}
