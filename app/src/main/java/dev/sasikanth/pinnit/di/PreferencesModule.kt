package dev.sasikanth.pinnit.di

import android.app.Application
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.data.preferences.appPreferencesStore

@Module
object PreferencesModule {

  @AppScope
  @Provides
  fun providesAppPreferencesStore(application: Application): DataStore<AppPreferences> {
    return application.appPreferencesStore
  }
}
