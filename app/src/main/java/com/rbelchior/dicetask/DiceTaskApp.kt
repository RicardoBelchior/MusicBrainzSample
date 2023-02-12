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

        // TODO: Schedule background task to remove unsaved artists from db
        //  Either directly here on a background thread or using a JobScheduler or WorkManager.
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
