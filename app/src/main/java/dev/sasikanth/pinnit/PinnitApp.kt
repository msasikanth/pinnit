package dev.sasikanth.pinnit

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.work.Configuration
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.di.AppComponent
import dev.sasikanth.pinnit.di.ComponentProvider
import dev.sasikanth.pinnit.di.DaggerAppComponent
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class PinnitApp : Application(), ComponentProvider, Configuration.Provider {

  @Inject
  lateinit var appPreferencesStore: DataStore<AppPreferences>

  @Inject
  lateinit var configuration: Configuration

  @Inject
  lateinit var dispatcherProvider: DispatcherProvider

  private val mainScope by lazy {
    CoroutineScope(dispatcherProvider.main)
  }

  override val component: AppComponent by lazy {
    DaggerAppComponent.factory().create(this)
  }

  override fun onCreate() {
    super.onCreate()
    component.inject(this)

    appPreferencesStore
      .data
      .map { it.theme }
      .onEach(::setAppTheme)
      .launchIn(mainScope)
  }

  override fun getWorkManagerConfiguration(): Configuration {
    return configuration
  }

  private fun setAppTheme(theme: AppPreferences.Theme) = when (theme) {
    AppPreferences.Theme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    AppPreferences.Theme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    AppPreferences.Theme.AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
  }
}
