package dev.sasikanth.pinnit

import android.app.Application
import dev.sasikanth.pinnit.data.PinnitPreferences
import dev.sasikanth.pinnit.di.AppComponent
import dev.sasikanth.pinnit.di.ComponentProvider
import dev.sasikanth.pinnit.di.DaggerAppComponent
import javax.inject.Inject

class PinnitApp : Application(), ComponentProvider {

  // Injecting this to trigger the init
  // function of PinnitPreferences to update theme
  @Inject
  lateinit var pinnitPreferences: PinnitPreferences

  override val component: AppComponent by lazy {
    DaggerAppComponent.factory().create(this)
  }

  override fun onCreate() {
    super.onCreate()
    component.inject(this)
  }
}
