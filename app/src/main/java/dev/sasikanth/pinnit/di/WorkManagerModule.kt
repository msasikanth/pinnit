package dev.sasikanth.pinnit.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object WorkManagerModule {

  @Singleton
  @Provides
  fun providesWorkManager(
    @ApplicationContext context: Context
  ): WorkManager = WorkManager.getInstance(context)

  @Singleton
  @Provides
  fun providesWorkManagerConfiguration(
    workerFactory: HiltWorkerFactory
  ): Configuration {
    return Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .build()
  }
}
