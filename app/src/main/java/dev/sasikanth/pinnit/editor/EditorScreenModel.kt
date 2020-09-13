package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification
import java.util.UUID

data class EditorScreenModel(
  val notificationUuid: UUID?,
  val notification: PinnitNotification?,
  val title: String?,
  val content: String?
) {

  companion object {
    fun default(notificationUuid: UUID?, content: String?) = EditorScreenModel(
      notificationUuid = notificationUuid,
      notification = null,
      title = null,
      content = content
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

  fun titleChanged(title: String?): EditorScreenModel {
    return copy(title = title)
  }

  fun contentChanged(content: String?): EditorScreenModel {
    return copy(content = content)
  }

  fun notificationLoaded(notification: PinnitNotification): EditorScreenModel {
    return copy(notification = notification)
  }
}
