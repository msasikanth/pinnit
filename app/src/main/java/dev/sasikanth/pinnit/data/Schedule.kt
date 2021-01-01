package dev.sasikanth.pinnit.data

import android.os.Parcelable
import androidx.room.TypeConverter
import dev.sasikanth.pinnit.utils.UserClock
import kotlinx.android.parcel.Parcelize
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Parcelize
data class Schedule(
  val scheduleDate: LocalDate?,
  val scheduleTime: LocalTime?,
  val scheduleType: ScheduleType?
) : Parcelable {

  fun removeScheduleRepeat(): Schedule {
    return copy(scheduleType = null)
  }

  fun addScheduleRepeat(): Schedule {
    return copy(scheduleType = ScheduleType.Daily)
  }

  fun scheduleDateChanged(date: LocalDate): Schedule {
    return copy(scheduleDate = date)
  }

  fun scheduleTimeChanged(time: LocalTime?): Schedule {
    return copy(scheduleTime = time)
  }

  fun scheduleTypeChanged(scheduleType: ScheduleType): Schedule {
    return copy(scheduleType = scheduleType)
  }

  companion object {

    fun default(userClock: UserClock): Schedule {
      val userDateTime = LocalDateTime.now(userClock)
        .plus(Duration.ofMinutes(10))

      return Schedule(
        scheduleDate = userDateTime.toLocalDate(),
        scheduleTime = userDateTime.toLocalTime(),
        scheduleType = ScheduleType.Daily
      )
    }
  }
}

enum class ScheduleType {
  Daily, Weekly, Monthly
}

class ScheduleTypeConverter {

  @TypeConverter
  fun toScheduleType(value: String?): ScheduleType? {
    return if (value != null) {
      enumValueOf<ScheduleType>(value)
    } else {
      null
    }
  }

  @TypeConverter
  fun fromScheduleType(scheduleType: ScheduleType?): String? {
    return scheduleType?.toString()
  }
}
