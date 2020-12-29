package dev.sasikanth.pinnit

import android.app.Application
import androidx.work.Configuration
import dev.sasikanth.pinnit.data.PinnitPreferences
import dev.sasikanth.pinnit.di.AppComponent
import dev.sasikanth.pinnit.di.ComponentProvider
import dev.sasikanth.pinnit.di.DaggerAppComponent
import javax.inject.Inject

class PinnitApp : Application(), ComponentProvider, Configuration.Provider {

  @Inject
  lateinit var pinnitPreferences: PinnitPreferences

  @Inject
  lateinit var configuration: Configuration

  override val component: AppComponent by lazy {
    DaggerAppComponent.factory().create(this)
  }

  override fun onCreate() {
    super.onCreate()
    component.inject(this)
    pinnitPreferences.initTheme()
  }

  override fun getWorkManagerConfiguration(): Configuration {
    return configuration
  }
}
