package dev.sasikanth.pinnit.notifications

import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.data.PinnitNotification
import javax.inject.Singleton

@Module
object NotificationModule {

  @Singleton
  @Provides
  fun providesNotificationDao(appDatabase: AppDatabase): PinnitNotification.RoomDao {
    return appDatabase.notificationDao()
  }
}
