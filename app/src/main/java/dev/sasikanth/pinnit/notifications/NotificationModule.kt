package dev.sasikanth.pinnit.notifications

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.data.PinnitNotification
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NotificationModule {

  @Singleton
  @Provides
  fun providesNotificationDao(appDatabase: AppDatabase): PinnitNotification.RoomDao {
    return appDatabase.notificationDao()
  }
}
