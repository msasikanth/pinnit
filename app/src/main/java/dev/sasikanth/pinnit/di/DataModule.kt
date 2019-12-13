package dev.sasikanth.pinnit.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.source.local.PinnitDatabase
import dev.sasikanth.pinnit.data.source.local.PinnitLocalDataSource
import javax.inject.Singleton

@Module
object DataModule {

  @Provides
  @Singleton
  fun providesRoomDatabase(context: Context) = PinnitDatabase.createDatabase(context)

  @Provides
  @Singleton
  fun providesNotifLocalDataSource(pinnitDatabase: PinnitDatabase) = PinnitLocalDataSource(
      pinnitDao = pinnitDatabase.pinnitDao
  )
}
