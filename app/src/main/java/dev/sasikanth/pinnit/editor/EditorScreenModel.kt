package dev.sasikanth.pinnit.editor

import java.util.UUID

data class EditorScreenModel(
  val notificationUuid: UUID?,
  val title: String?,
  val content: String?
) {

  companion object {
    fun default(uuid: UUID?) = EditorScreenModel(
      notificationUuid = uuid,
      title = null,
      content = null
    )
  }

  fun titleChanged(title: String): EditorScreenModel {
    return copy(title = title)
  }

  fun contentChanged(content: String?): EditorScreenModel {
    return copy(content = content)
  }
}
