package dev.sasikanth.pinnit.editor

import android.os.Parcelable
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import dev.sasikanth.pinnit.data.ScheduleType
import dev.sasikanth.pinnit.editor.ScheduleValidator.Result
import dev.sasikanth.pinnit.editor.ScheduleValidator.Result.Valid
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Parcelize
data class EditorScreenModel(
  val notificationUuid: UUID?,
  val notification: PinnitNotification?,
  val title: String?,
  val content: String?,
  val schedule: Schedule?,
  val scheduleValidationResult: Result?
) : Parcelable {

  companion object {
    fun default(notificationUuid: UUID?, title: String?, content: String?) = EditorScreenModel(
      notificationUuid = notificationUuid,
      notification = null,
      title = title,
      content = content,
      schedule = null,
      scheduleValidationResult = null
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
      return notification?.equalsTitleAndContent(title, content)?.not()
        ?: (hasNotificationTitle || hasNotificationContent)
    }

  val hasScheduleChanged: Boolean
    get() = notification?.schedule != schedule

  val isNotificationLoaded: Boolean
    get() = notification != null

  val hasNotificationTitle: Boolean
    get() = title.isNullOrBlank().not()

  private val hasNotificationContent: Boolean
    get() = content.isNullOrBlank().not()

  val hasSchedule: Boolean
    get() = schedule != null

  val hasValidScheduleResult: Boolean
    get() = scheduleValidationResult == null || scheduleValidationResult == Valid

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
    return copy(schedule = schedule, scheduleValidationResult = null)
  }

  fun removeSchedule(): EditorScreenModel {
    return copy(schedule = null, scheduleValidationResult = null)
  }

  fun removeScheduleRepeat(): EditorScreenModel {
    return copy(schedule = schedule?.removeScheduleRepeat())
  }

  fun addScheduleRepeat(): EditorScreenModel {
    return copy(schedule = schedule?.addScheduleRepeat())
  }

  fun scheduleDateChanged(date: LocalDate): EditorScreenModel {
    return copy(schedule = schedule?.scheduleDateChanged(date))
  }

  fun scheduleTimeChanged(updatedLocalTime: LocalTime?): EditorScreenModel {
    return copy(schedule = schedule?.scheduleTimeChanged(updatedLocalTime))
  }

  fun scheduleTypeChanged(scheduleType: ScheduleType): EditorScreenModel {
    return copy(schedule = schedule?.scheduleTypeChanged(scheduleType))
  }

  fun scheduleValidated(result: Result): EditorScreenModel {
    return copy(scheduleValidationResult = result)
  }
}
