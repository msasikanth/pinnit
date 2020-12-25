package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

sealed class EditorScreenEffect

data class LoadNotification(val uuid: UUID) : EditorScreenEffect()

data class SetTitleAndContent(val title: String?, val content: String?) : EditorScreenEffect()

data class SaveNotification(
  val title: String,
  val content: String?,
  val schedule: Schedule?
) : EditorScreenEffect()

data class UpdateNotification(
  val notificationUuid: UUID,
  val title: String,
  val content: String?,
  val schedule: Schedule?
) : EditorScreenEffect()

object CloseEditor : EditorScreenEffect()

object ShowConfirmExitEditor : EditorScreenEffect()

data class DeleteNotification(val notification: PinnitNotification) : EditorScreenEffect()

object ShowConfirmDelete : EditorScreenEffect()

data class ShowDatePicker(val date: LocalDate) : EditorScreenEffect()

data class ShowTimePicker(var time: LocalTime) : EditorScreenEffect()

data class ShowNotification(val notification: PinnitNotification) : EditorScreenEffect()

data class ScheduleNotification(val notification: PinnitNotification) : EditorScreenEffect()
