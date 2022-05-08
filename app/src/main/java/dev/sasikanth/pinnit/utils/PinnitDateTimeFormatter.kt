package dev.sasikanth.pinnit.utils

import dev.sasikanth.pinnit.di.DateTimeFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class PinnitDateTimeFormatter @Inject constructor(
  private val userClock: UserClock,
  @DateTimeFormat(DateTimeFormat.Type.ScheduleDateFormat)
  private val dateFormatter: DateTimeFormatter,
  @DateTimeFormat(DateTimeFormat.Type.ScheduleTimeFormat)
  private val timeFormatter: DateTimeFormatter
) {

  private val today = LocalDateTime.now(userClock)
  private val yesterday = today.minusDays(1)
  private val tomorrow = today.plusDays(1)

  fun relativeTimeStamp(
    time: Instant
  ): String {
    val zoneDateTime = time.atZone(userClock.zone)

    return when {
      isMoreThanOneDayOld(zoneDateTime) -> "${today.dayOfYear - zoneDateTime.dayOfYear}D ago"
      isOneDayOld(zoneDateTime) -> "Yesterday"
      isWithInSameMinute(zoneDateTime) -> "Now"
      isWithInSameHour(zoneDateTime) -> "${today.minute - zoneDateTime.minute}m ago"
      isWithInSameDay(zoneDateTime) -> "${today.hour - zoneDateTime.hour}H ago"
      else -> "$time"
    }
  }

  private fun isMoreThanOneDayOld(date: ZonedDateTime): Boolean {
    return yesterday.toLocalDate().isAfter(date.toLocalDate())
  }

  private fun isOneDayOld(date: ZonedDateTime): Boolean {
    return yesterday.toLocalDate() == date.toLocalDate()
  }

  private fun isWithInSameDay(date: ZonedDateTime): Boolean {
    return today.toLocalDate() == date.toLocalDate()
  }

  private fun isWithInSameHour(dateTime: ZonedDateTime): Boolean {
    return today.hour == dateTime.hour
  }

  private fun isWithInSameMinute(time: ZonedDateTime): Boolean {
    return today.hour == time.hour && today.minute == time.minute
  }

  fun dateAndTimeString(date: LocalDate, time: LocalTime): String {
    return when {
      today.dayOfYear == date.dayOfYear -> "Today - ${timeFormatter.format(time)}"
      yesterday.toLocalDate() == date -> "Yesterday - ${timeFormatter.format(time)}"
      tomorrow.toLocalDate() == date -> "Tomorrow - ${timeFormatter.format(time)}"
      else -> "${dateFormatter.format(date)} - ${timeFormatter.format(time)}"
    }
  }
}
