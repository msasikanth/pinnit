package dev.sasikanth.notif.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
object PreferenceModule {

    @Provides
    @JvmStatic
    fun providesSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(
            context
        )
}
