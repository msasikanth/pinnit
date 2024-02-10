package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import dev.sasikanth.pinnit.data.ScheduleType
import java.time.LocalDate
import java.time.LocalTime

sealed class EditorScreenEvent

data class NotificationLoaded(val notification: PinnitNotification) : EditorScreenEvent()

data class TitleChanged(val title: String) : EditorScreenEvent()

data class ContentChanged(val content: String?) : EditorScreenEvent()

data object SaveClicked : EditorScreenEvent()

data object BackClicked : EditorScreenEvent()

data object ConfirmedExit : EditorScreenEvent()

data object DeleteNotificationClicked : EditorScreenEvent()

data object ConfirmDeleteNotification : EditorScreenEvent()

data class AddScheduleClicked(val schedule: Schedule) : EditorScreenEvent()

data object RemoveScheduleClicked : EditorScreenEvent()

data object ScheduleRepeatUnchecked : EditorScreenEvent()

data object ScheduleRepeatChecked : EditorScreenEvent()

data object ScheduleDateClicked : EditorScreenEvent()

data object ScheduleTimeClicked : EditorScreenEvent()

data class ScheduleDateChanged(val date: LocalDate) : EditorScreenEvent()

data class ScheduleTimeChanged(val time: LocalTime) : EditorScreenEvent()

data class ScheduleTypeChanged(val scheduleType: ScheduleType) : EditorScreenEvent()

data class NotificationSaved(val notification: PinnitNotification) : EditorScreenEvent()

data class NotificationUpdated(val updatedNotification: PinnitNotification) : EditorScreenEvent()

data class ScheduleValidated(val result: ScheduleValidator.Result) : EditorScreenEvent()
