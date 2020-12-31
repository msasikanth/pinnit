package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import dev.sasikanth.pinnit.di.AppScope
import dev.sasikanth.pinnit.utils.UtcClock
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@AppScope
class NotificationRepository @Inject constructor(
  private val notificationDao: PinnitNotification.RoomDao,
  private val utcClock: UtcClock
) {

  suspend fun save(
    title: String,
    content: String? = null,
    isPinned: Boolean = true,
    schedule: Schedule? = null,
    uuid: UUID = UUID.randomUUID()
  ): PinnitNotification {
    val notification = PinnitNotification(
      uuid = uuid,
      title = title,
      content = content,
      isPinned = isPinned,
      schedule = schedule,
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock),
      deletedAt = null
    )
    notificationDao.save(listOf(notification))
    return notification
  }

  suspend fun save(notifications: List<PinnitNotification>) {
    notificationDao.save(notifications)
  }

  suspend fun updateNotification(notification: PinnitNotification): PinnitNotification {
    val updatedNotification = notification.copy(
      updatedAt = Instant.now(utcClock)
    )
    notificationDao.save(listOf(updatedNotification))
    return updatedNotification
  }

  suspend fun notification(uuid: UUID): PinnitNotification {
    return notificationDao.notification(uuid)
  }

  suspend fun updatePinStatus(notificationUuid: UUID, isPinned: Boolean) {
    notificationDao.updatePinStatus(notificationUuid, isPinned)
  }

  fun notifications(): Flow<List<PinnitNotification>> {
    return notificationDao.notifications()
  }

  suspend fun pinnedNotifications(): List<PinnitNotification> {
    return notificationDao.pinnedNotifications()
  }

  suspend fun deleteNotification(notification: PinnitNotification): PinnitNotification {
    val deletedNotification = notification.copy(
      deletedAt = Instant.now(utcClock)
    )
    notificationDao.save(listOf(deletedNotification))
    return deletedNotification
  }

  suspend fun undoNotificationDelete(notification: PinnitNotification) {
    val undidNotification = notification.copy(
      deletedAt = null
    )
    notificationDao.save(listOf(undidNotification))
  }

  suspend fun removeSchedule(notificationId: UUID) {
    notificationDao.removeSchedule(notificationId)
  }
}
