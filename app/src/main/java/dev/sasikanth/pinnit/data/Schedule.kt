package dev.sasikanth.pinnit.data

import android.os.Parcelable
import androidx.room.TypeConverter
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalTime

@Parcelize
data class Schedule(
  val scheduleDate: LocalDate?,
  val scheduleTime: LocalTime?,
  val scheduleType: ScheduleType?
) : Parcelable

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
