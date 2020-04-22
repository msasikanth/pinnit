package dev.sasikanth.pinnit.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.AppDatabase

@Module
object TestAppModule {

  @Provides
  @AppScope
  fun providesTestAppDatabase(
    application: Application
  ): AppDatabase {
    return Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java)
      .build()
  }
}
