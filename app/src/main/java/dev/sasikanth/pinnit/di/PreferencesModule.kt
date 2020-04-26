package dev.sasikanth.pinnit.di

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
object PreferencesModule {

  @AppScope
  @Provides
  fun providesAppPreferences(application: Application): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(application)
  }
}
