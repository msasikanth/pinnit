package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import java.util.UUID

data class EditorScreenModel(
  val notificationUuid: UUID?,
  val notification: PinnitNotification?,
  val title: String?,
  val content: String?,
  val schedule: Schedule?
) {

  companion object {
    fun default(notificationUuid: UUID?, title: String?, content: String?) = EditorScreenModel(
      notificationUuid = notificationUuid,
      notification = null,
      title = title,
      content = content,
      schedule = null
    )
  }

  /**
   * Compares the editor title and content with the [PinnitNotification]
   * title and content. It both title and content are same in [EditorScreenModel]
   * & [PinnitNotification] we can say content is not changed or else content is
   * changed. By default we set content to be changed.
   */
  val hasTitleAndContentChanged: Boolean
    get() {
      return notification?.equalsTitleAndContent(title, content)?.not() ?: (!title.isNullOrBlank() || !content.isNullOrBlank())
    }

  val isNotificationLoaded: Boolean
    get() = notification != null

  val hasNotificationTitle: Boolean
    get() = title.isNullOrBlank().not()

  val hasSchedule: Boolean
    get() = schedule != null

  fun titleChanged(title: String?): EditorScreenModel {
    return copy(title = title)
  }

  fun contentChanged(content: String?): EditorScreenModel {
    return copy(content = content)
  }

  fun notificationLoaded(notification: PinnitNotification): EditorScreenModel {
    return copy(notification = notification)
  }

  fun scheduleLoaded(schedule: Schedule?): EditorScreenModel {
    return copy(schedule = schedule)
  }

  fun addSchedule(schedule: Schedule): EditorScreenModel {
    return copy(schedule = schedule)
  }

  fun removeSchedule(): EditorScreenModel {
    return copy(schedule = null)
  }
}
