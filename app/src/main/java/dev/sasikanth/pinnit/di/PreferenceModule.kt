package dev.sasikanth.pinnit.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object PreferenceModule {

    @Provides
    @Singleton
    fun providesSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
}
