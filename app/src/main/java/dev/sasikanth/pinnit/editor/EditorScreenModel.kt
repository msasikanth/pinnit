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

  fun titleChanged(title: String?): EditorScreenModel {
    return copy(title = title)
  }

  fun contentChanged(content: String?): EditorScreenModel {
    return copy(content = content)
  }
}
