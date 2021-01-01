package dev.sasikanth.pinnit.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import dev.sasikanth.pinnit.data.ScheduleType
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

class ScheduleWorker(
  context: Context,
  parameters: WorkerParameters,
  private val repository: NotificationRepository,
  private val notificationUtil: NotificationUtil,
  private val userClock: UserClock,
  private val dispatcherProvider: DispatcherProvider
) : CoroutineWorker(context, parameters) {

  companion object {
    const val INPUT_NOTIFICATION_UUID = "notification_uuid"
    private const val SCHEDULE_TAG_PREFIX = "scheduled-notification"

    fun scheduleNotificationRequest(
      notificationUuid: UUID,
      schedule: Schedule,
      userClock: UserClock
    ): OneTimeWorkRequest {
      val currentDateTime = LocalDateTime.now(userClock)
      val scheduledAt = schedule.scheduleDate!!.atTime(schedule.scheduleTime!!)
      val initialDelay = Duration.between(currentDateTime, scheduledAt)

      val inputData = workDataOf(
        INPUT_NOTIFICATION_UUID to notificationUuid.toString()
      )

      return OneTimeWorkRequestBuilder<ScheduleWorker>()
        .setInputData(inputData)
        .setInitialDelay(initialDelay.toMillis(), TimeUnit.MILLISECONDS)
        .addTag(tag(notificationUuid = notificationUuid))
        .build()
    }

    fun tag(notificationUuid: UUID): String {
      return "$SCHEDULE_TAG_PREFIX-${notificationUuid}"
    }
  }

  override suspend fun doWork(): Result {
    val notificationUuidString = inputData.getString(INPUT_NOTIFICATION_UUID)
    val notificationUuid = try {
      UUID.fromString(notificationUuidString)
    } catch (e: IllegalArgumentException) {
      return Result.failure()
    }

    withContext(dispatcherProvider.io) {
      // If the notification is not already pinned, then pin the notification
      repository.updatePinStatus(notificationUuid, true)

      // Getting notification from UUID
      val notification = repository.notification(notificationUuid)

      // Show notification (If it's already being shown, it will just refresh the notification)
      notificationUtil.showNotification(notification)

      // If there is no schedule or schedule type associated with schedule is not set,
      // there is no point rescheduling it. So we will skip rescheduling it.
      if (notification.schedule?.scheduleType != null) {
        // Update scheduleAt to next scheduled time based on schedule type.
        reschedule(notification)
      }
    }

    return Result.success()
  }

  private suspend fun reschedule(notification: PinnitNotification) {
    val schedule = notification.schedule!!
    val scheduleType = schedule.scheduleType!!

    val scheduledAt = schedule.scheduleDate!!.atTime(schedule.scheduleTime!!)
    val updatedScheduledAt = when (scheduleType) {
      ScheduleType.Daily -> scheduledAt.plusDays(1L)
      ScheduleType.Weekly -> scheduledAt.plusWeeks(1L)
      ScheduleType.Monthly -> scheduledAt.plusMonths(1L)
    }

    val updatedNotification = notification.copy(
      schedule = schedule.copy(
        scheduleDate = updatedScheduledAt.toLocalDate(),
        scheduleTime = updatedScheduledAt.toLocalTime()
      )
    )

    repository.updateNotification(updatedNotification)

    scheduleNotificationRequest(
      notificationUuid = updatedNotification.uuid,
      schedule = updatedNotification.schedule!!,
      userClock = userClock
    )
  }
}
