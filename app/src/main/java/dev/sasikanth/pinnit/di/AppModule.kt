package dev.sasikanth.pinnit.di

import android.app.Application
import androidx.room.Room
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.data.migrations.Migration_1_2
import dev.sasikanth.pinnit.notifications.NotificationModule
import dev.sasikanth.pinnit.utils.CoroutineDispatcherProvider
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.RealUserClock
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import dev.sasikanth.pinnit.worker.PinnitWorkerFactory
import java.time.ZoneId

@Module(
  includes = [
    NotificationModule::class,
    AssistedInjectModule::class,
    PreferencesModule::class
  ]
)
object AppModule {

  @AppScope
  @Provides
  fun providesAppDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(application, AppDatabase::class.java, "pinnit-db")
      .addMigrations(Migration_1_2)
      .build()
  }

  @AppScope
  @Provides
  fun providesUtcClock(): UtcClock = UtcClock()

  @AppScope
  @Provides
  fun providesUserClock(userTimeZone: ZoneId): UserClock = RealUserClock(userTimeZone)

  @AppScope
  @Provides
  fun providesDispatcherProvider(): DispatcherProvider = CoroutineDispatcherProvider()

  @AppScope
  @Provides
  fun providesSystemDefaultZone(): ZoneId = ZoneId.systemDefault()

  @AppScope
  @Provides
  fun providesWorkManager(application: Application): WorkManager = WorkManager.getInstance(application)

  @AppScope
  @Provides
  fun providesWorkManagerConfiguration(
    pinnitWorkerFactory: PinnitWorkerFactory
  ): Configuration {
    return Configuration.Builder()
      .setWorkerFactory(pinnitWorkerFactory)
      .build()
  }
}
