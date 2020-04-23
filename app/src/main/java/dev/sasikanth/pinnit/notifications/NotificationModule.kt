package dev.sasikanth.pinnit.notifications

import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.di.AppScope

@Module
object NotificationModule {

  @AppScope
  @Provides
  fun providesNotificationDao(appDatabase: AppDatabase): PinnitNotification.RoomDao {
    return appDatabase.notificationDao()
  }
}
