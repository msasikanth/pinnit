package dev.sasikanth.notif.di

import android.content.Context
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides

@Module
object PreferenceModule {

    @Provides
    @JvmStatic
    fun providesSharedPreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(
        context
    )
}
