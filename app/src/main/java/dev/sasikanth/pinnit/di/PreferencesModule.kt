package dev.sasikanth.pinnit.di

import android.app.Application
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.data.preferences.appPreferencesStore
import javax.inject.Singleton

@Module
object PreferencesModule {

  @Singleton
  @Provides
  fun providesAppPreferencesStore(application: Application): DataStore<AppPreferences> {
    return application.appPreferencesStore
  }
}
