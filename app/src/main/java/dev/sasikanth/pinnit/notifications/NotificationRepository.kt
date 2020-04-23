package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.di.AppScope
import dev.sasikanth.pinnit.utils.UtcClock
import org.threeten.bp.Instant
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
    isPinned: Boolean = false,
    uuid: UUID = UUID.randomUUID()
  ): PinnitNotification {
    val notification = PinnitNotification(
      uuid = uuid,
      title = title,
      content = content,
      isPinned = isPinned,
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock),
      deletedAt = null
    )
    notificationDao.save(listOf(notification))
    return notification
  }
}
