package dev.sasikanth.notif

import android.app.Application
import dev.sasikanth.notif.di.notifModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class NotifApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@NotifApp)
            modules(notifModules)
        }
    }
}
