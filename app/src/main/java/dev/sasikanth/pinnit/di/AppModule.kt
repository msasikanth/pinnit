package dev.sasikanth.pinnit.di

import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides

@Module
object AppModule {

  @Provides
  fun providesPackageManager(context: Context): PackageManager = context.packageManager
}
