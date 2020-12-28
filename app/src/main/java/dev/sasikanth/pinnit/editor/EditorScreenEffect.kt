package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification
import java.time.LocalDate
import java.util.UUID

sealed class EditorScreenEffect

data class LoadNotification(val uuid: UUID) : EditorScreenEffect()

data class SetTitleAndContent(val title: String?, val content: String?) : EditorScreenEffect()

data class SaveNotificationAndCloseEditor(val title: String, val content: String?) : EditorScreenEffect()

data class UpdateNotificationAndCloseEditor(
  val notificationUuid: UUID,
  val title: String,
  val content: String?,
  val showAndroidNotification: Boolean
) : EditorScreenEffect()

object CloseEditor : EditorScreenEffect()

object ShowConfirmExitEditor : EditorScreenEffect()

data class DeleteNotification(val notification: PinnitNotification) : EditorScreenEffect()

object ShowConfirmDelete : EditorScreenEffect()

data class ShowDatePicker(val date: LocalDate) : EditorScreenEffect()
