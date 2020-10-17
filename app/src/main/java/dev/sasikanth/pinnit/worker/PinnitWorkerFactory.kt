package dev.sasikanth.pinnit.worker

import androidx.work.DelegatingWorkerFactory
import dev.sasikanth.pinnit.di.AppScope
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import javax.inject.Inject

@AppScope
class PinnitWorkerFactory @Inject constructor(
  notificationRepository: NotificationRepository,
  notificationUtil: NotificationUtil,
  userClock: UserClock,
  dispatcherProvider: DispatcherProvider,
) : DelegatingWorkerFactory() {
  init {
    addFactory(ScheduleWorkerFactory(notificationRepository, notificationUtil, userClock, dispatcherProvider))
  }
}
