package dev.sasikanth.pinnit.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object PreferenceModule {

    @Provides
    @JvmStatic
    @Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(
            context
        )
}
