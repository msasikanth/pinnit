package dev.sasikanth.pinnit.scheduler

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.worker.ScheduleWorker
import java.util.UUID
import javax.inject.Inject

class PinnitNotificationScheduler @Inject constructor(
  private val workManager: WorkManager,
  private val userClock: UserClock
) {

  fun scheduleNotification(notification: PinnitNotification) {
    if (notification.schedule != null) {
      val workRequest = ScheduleWorker.scheduleNotificationRequest(
        notificationUuid = notification.uuid,
        schedule = notification.schedule,
        userClock = userClock
      )

      workManager
        .enqueueUniqueWork(
          ScheduleWorker.tag(notificationUuid = notification.uuid),
          ExistingWorkPolicy.REPLACE,
          workRequest
        )
    }
  }

  fun cancel(notificationUuid: UUID) {
    workManager.cancelAllWorkByTag(ScheduleWorker.tag(notificationUuid = notificationUuid))
  }
}
