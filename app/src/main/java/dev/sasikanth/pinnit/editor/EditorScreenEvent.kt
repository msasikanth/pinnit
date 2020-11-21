package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import java.time.LocalDate
import java.time.LocalTime

sealed class EditorScreenEvent

data class NotificationLoaded(val notification: PinnitNotification) : EditorScreenEvent()

data class TitleChanged(val title: String) : EditorScreenEvent()

data class ContentChanged(val content: String?) : EditorScreenEvent()

object SaveClicked : EditorScreenEvent()

object BackClicked : EditorScreenEvent()

object ConfirmedExit : EditorScreenEvent()

object DeleteNotificationClicked : EditorScreenEvent()

object ConfirmDeleteNotification : EditorScreenEvent()

data class AddScheduleClicked(val schedule: Schedule) : EditorScreenEvent()

object RemoveScheduleClicked : EditorScreenEvent()

object ScheduleRepeatUnchecked : EditorScreenEvent()

object ScheduleRepeatChecked : EditorScreenEvent()

object ScheduleDateClicked : EditorScreenEvent()

object ScheduleTimeClicked : EditorScreenEvent()

data class ScheduleDateChanged(val date: LocalDate) : EditorScreenEvent()

data class ScheduleTimeChanged(val time: LocalTime) : EditorScreenEvent()
