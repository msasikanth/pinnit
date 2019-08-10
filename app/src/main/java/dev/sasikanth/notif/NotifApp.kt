package dev.sasikanth.notif

import android.app.Application
import dev.sasikanth.notif.di.ComponentProvider
import dev.sasikanth.notif.di.DaggerNotifAppComponent
import dev.sasikanth.notif.di.NotifAppComponent
import timber.log.Timber

class NotifApp : Application(), ComponentProvider {

    override val component: NotifAppComponent
        get() = DaggerNotifAppComponent.factory().create(this)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
