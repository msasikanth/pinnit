package dev.sasikanth.pinnit.di

import android.content.Context
import android.text.format.DateFormat
import androidx.room.Room
import com.spotify.mobius.android.runners.MainThreadWorkRunner
import com.spotify.mobius.runners.WorkRunner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.data.migrations.Migration_1_2
import dev.sasikanth.pinnit.utils.CoroutineDispatcherProvider
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.RealUserClock
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import java.time.ZoneId
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

  @Singleton
  @Provides
  fun providesAppDatabase(
    @ApplicationContext context: Context
  ): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "pinnit-db")
      .addMigrations(Migration_1_2)
      .build()
  }

  @Singleton
  @Provides
  fun providesUtcClock(): UtcClock = UtcClock()

  @Singleton
  @Provides
  fun providesUserClock(userTimeZone: ZoneId): UserClock = RealUserClock(userTimeZone)

  @Singleton
  @Provides
  fun providesDispatcherProvider(): DispatcherProvider = CoroutineDispatcherProvider()

  @Singleton
  @Provides
  fun providesSystemDefaultZone(): ZoneId = ZoneId.systemDefault()

  @Singleton
  @Provides
  fun providesWorkRunner(): WorkRunner = MainThreadWorkRunner.create()
}
