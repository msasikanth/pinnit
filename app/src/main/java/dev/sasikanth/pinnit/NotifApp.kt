package dev.sasikanth.pinnit

import android.app.Application
import dev.sasikanth.pinnit.di.ComponentProvider
import dev.sasikanth.pinnit.di.DaggerNotifAppComponent
import dev.sasikanth.pinnit.di.NotifAppComponent
import timber.log.Timber

class NotifApp : Application(), ComponentProvider {

    override val component: NotifAppComponent by lazy {
        DaggerNotifAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
