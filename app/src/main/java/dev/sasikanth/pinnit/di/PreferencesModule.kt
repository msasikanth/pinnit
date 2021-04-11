package dev.sasikanth.pinnit.di

import android.app.Application
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.data.preferences.appPreferencesStore

@Module
object PreferencesModule {

  @AppScope
  @Provides
  fun providesAppPreferences(application: Application): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(application)
  }

  @AppScope
  @Provides
  fun providesAppPreferencesStore(application: Application): DataStore<AppPreferences> {
    return application.appPreferencesStore
  }
}
