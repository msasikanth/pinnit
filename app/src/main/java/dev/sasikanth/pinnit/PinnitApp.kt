package dev.sasikanth.pinnit

import android.app.Application
import dev.sasikanth.pinnit.di.AppComponent
import dev.sasikanth.pinnit.di.ComponentProvider
import dev.sasikanth.pinnit.di.DaggerAppComponent

class PinnitApp : Application(), ComponentProvider {
  override val component: AppComponent by lazy {
    DaggerAppComponent.factory().create(this)
  }
}
