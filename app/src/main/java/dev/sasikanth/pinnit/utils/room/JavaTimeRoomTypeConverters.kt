package dev.sasikanth.pinnit.utils.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class InstantRoomTypeConverter {

  @TypeConverter
  fun toInstant(value: String?): Instant? {
    return value?.let { Instant.parse(value) }
  }

  @TypeConverter
  fun fromInstant(instant: Instant?): String? {
    return instant?.toString()
  }
}

class LocalDateRoomTypeConverter {

  @TypeConverter
  fun toLocalDate(value: String?): LocalDate? {
    return value?.let { LocalDate.parse(value) }
  }

  @TypeConverter
  fun fromLocalDate(localDate: LocalDate?): String? {
    return localDate?.toString()
  }
}

class LocalTimeRoomTypeConverter {

  @TypeConverter
  fun toLocalTime(value: String?): LocalTime? {
    return value?.let { LocalTime.parse(value) }
  }

  @TypeConverter
  fun fromLocalTime(localTime: LocalTime?): String? {
    return localTime?.toString()
  }
}
