package dev.sasikanth.pinnit.di

import android.content.Context
import androidx.room.Room
import com.spotify.mobius.runners.ImmediateWorkRunner
import com.spotify.mobius.runners.WorkRunner
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.utils.CoroutineDispatcherProvider
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import dev.sasikanth.sharedtestcode.utils.TestUserClock
import dev.sasikanth.sharedtestcode.utils.TestUtcClock
import java.time.ZoneId
import javax.inject.Singleton

@TestInstallIn(
  components = [SingletonComponent::class],
  replaces = [AppModule::class]
)
@Module
object TestAppModule {

  @Singleton
  @Provides
  fun providesTestAppDatabase(
    @ApplicationContext context: Context
  ): AppDatabase {
    return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .build()
  }

  @Singleton
  @Provides
  fun providesTestUtcClock(): TestUtcClock = TestUtcClock()

  @Singleton
  @Provides
  fun providesUtcClock(testUtcClock: TestUtcClock): UtcClock = testUtcClock

  @Singleton
  @Provides
  fun testUserClock(): TestUserClock {
    return TestUserClock()
  }

  @Singleton
  @Provides
  fun userClock(testUserClock: TestUserClock): UserClock = testUserClock

  @Singleton
  @Provides
  fun providesDispatcherProvider(): DispatcherProvider = CoroutineDispatcherProvider()

  @Singleton
  @Provides
  fun providesSystemDefaultZone(): ZoneId = ZoneId.systemDefault()

  @Singleton
  @Provides
  fun providesWorkRunner(): WorkRunner = ImmediateWorkRunner()
}
