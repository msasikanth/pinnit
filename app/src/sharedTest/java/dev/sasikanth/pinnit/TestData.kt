package dev.sasikanth.pinnit

import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.data.Schedule
import dev.sasikanth.pinnit.data.ScheduleType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

object TestData {
  fun notification(
    uuid: UUID = UUID.randomUUID(),
    title: String = "Notification Title",
    content: String? = null,
    isPinned: Boolean = false,
    createdAt: Instant = Instant.now(),
    updatedAt: Instant = Instant.now(),
    deletedAt: Instant? = null,
    scheduleDate: LocalDate = LocalDate.now(),
    scheduleTime: LocalTime = LocalTime.now(),
    scheduleType: ScheduleType? = ScheduleType.Daily,
    schedule: Schedule? = schedule(
      scheduleDate = scheduleDate,
      scheduleTime = scheduleTime,
      scheduleType = scheduleType
    )
  ): PinnitNotification {
    return PinnitNotification(
      uuid = uuid,
      title = title,
      content = content,
      isPinned = isPinned,
      createdAt = createdAt,
      updatedAt = updatedAt,
      deletedAt = deletedAt,
      schedule = schedule
    )
  }

  fun schedule(
    scheduleDate: LocalDate = LocalDate.now(),
    scheduleTime: LocalTime = LocalTime.now(),
    scheduleType: ScheduleType? = ScheduleType.Daily,
  ): Schedule {
    return Schedule(
      scheduleDate = scheduleDate,
      scheduleTime = scheduleTime,
      scheduleType = scheduleType
    )
  }
}
