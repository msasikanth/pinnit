package dev.sasikanth.pinnit

import android.app.Application
import dev.sasikanth.pinnit.di.ComponentProvider
import dev.sasikanth.pinnit.di.DaggerPinnitAppComponent
import dev.sasikanth.pinnit.di.PinnitAppComponent
import timber.log.Timber

class PinnitApp : Application(), ComponentProvider {

  override val component: PinnitAppComponent by lazy {
    DaggerPinnitAppComponent.factory().create(this)
  }

  override fun onCreate() {
    super.onCreate()
    Timber.plant(Timber.DebugTree())
  }
}
