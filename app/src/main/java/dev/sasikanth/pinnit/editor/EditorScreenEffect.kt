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
  val schedule: Schedule?,
  val canPinNotification: Boolean
) : EditorScreenEffect()

data class UpdateNotification(
  val notificationUuid: UUID,
  val title: String,
  val content: String?,
  val schedule: Schedule?
) : EditorScreenEffect()

data object CloseEditor : EditorScreenEffect()

data object ShowConfirmExitEditor : EditorScreenEffect()

data class DeleteNotification(val notification: PinnitNotification) : EditorScreenEffect()

data object ShowConfirmDelete : EditorScreenEffect()

data class ShowDatePicker(val date: LocalDate) : EditorScreenEffect()

data class ShowTimePicker(var time: LocalTime) : EditorScreenEffect()

data class ShowNotification(val notification: PinnitNotification) : EditorScreenEffect()

data class ScheduleNotification(val notification: PinnitNotification) : EditorScreenEffect()

data class CancelNotificationSchedule(val notificationId: UUID) : EditorScreenEffect()

data class ValidateSchedule(val scheduleDate: LocalDate, val scheduleTime: LocalTime) : EditorScreenEffect()
