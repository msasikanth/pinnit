package dev.sasikanth.notif.di

import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
object AppModule {

    @Provides
    @JvmStatic
    @Reusable
    fun providesPackageManager(context: Context): PackageManager = context.packageManager
}
