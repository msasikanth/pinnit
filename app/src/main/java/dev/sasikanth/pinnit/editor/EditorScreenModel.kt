package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification

data class EditorScreenModel(
  val notification: PinnitNotification?,
  val title: String?,
  val content: String?
) {

  companion object {
    fun default(notification: PinnitNotification?) = EditorScreenModel(
      notification = notification,
      title = null,
      content = null
    )
  }

  /**
   * Compares the editor title and content with the [PinnitNotification]
   * title and content. It both title and content are same in [EditorScreenModel]
   * & [PinnitNotification] we can say content is not changed or else content is
   * changed. By default we set content to be changed.
   */
  val isTitleAndContentChanged: Boolean
    get() {
      return notification?.equalsTitleAndContent(title, content)?.not() ?: (!title.isNullOrBlank() || !content.isNullOrBlank())
    }

  fun titleChanged(title: String?): EditorScreenModel {
    return copy(title = title)
  }

  fun contentChanged(content: String?): EditorScreenModel {
    return copy(content = content)
  }
}
