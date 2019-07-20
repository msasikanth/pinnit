package dev.sasikanth.notif

import android.app.Application
import dev.sasikanth.notif.di.notifModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotifApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NotifApp)
            modules(notifModules)
        }
    }
}
