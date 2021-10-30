package dev.sasikanth.pinnit.di

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sasikanth.pinnit.data.preferences.AppPreferences
import dev.sasikanth.pinnit.data.preferences.appPreferencesStore
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferencesModule {

  @Singleton
  @Provides
  fun providesAppPreferencesStore(
    @ApplicationContext context: Context
  ): DataStore<AppPreferences> {
    return context.appPreferencesStore
  }
}
