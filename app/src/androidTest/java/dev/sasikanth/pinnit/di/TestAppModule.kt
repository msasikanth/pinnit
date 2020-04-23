package dev.sasikanth.pinnit.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.utils.TestUtcClock
import dev.sasikanth.pinnit.utils.UtcClock

@Module
object TestAppModule {

  @AppScope
  @Provides
  fun providesTestAppDatabase(
    application: Application
  ): AppDatabase {
    return Room.inMemoryDatabaseBuilder(application, AppDatabase::class.java)
      .build()
  }

  @AppScope
  @Provides
  fun providesTestUtcClock(): TestUtcClock = TestUtcClock()

  @Provides
  fun providesUtcClock(testUtcClock: TestUtcClock): UtcClock = testUtcClock
}
