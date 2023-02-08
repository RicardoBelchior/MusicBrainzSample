package com.rbelchior.dicetask

import android.app.Application
import com.rbelchior.dicetask.di.*
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import org.koin.android.ext.koin.androidContext

class DiceTaskApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(
            this, minPriority = LogPriority.VERBOSE
        )

        startKoin()
    }

    private fun startKoin() {
        org.koin.core.context.startKoin {
            androidContext(applicationContext)

            modules(
                networkModule + utilsModule + databaseModule +
                        repositoryModule + useCaseModule + viewModelsModule
            )
        }
    }
}
