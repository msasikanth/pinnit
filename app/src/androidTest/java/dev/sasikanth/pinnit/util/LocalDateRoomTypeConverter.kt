package dev.sasikanth.pinnit.util

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class LocalDateRoomTypeConverter @Inject constructor() {

  companion object {
    private val formatter = DateTimeFormatter.ISO_DATE!!
  }

  @TypeConverter
  fun toLocalDate(value: String?): LocalDate? {
    return value?.let {
      return formatter.parse(value, LocalDate::from)
    }
  }

  @TypeConverter
  fun fromLocalDate(date: LocalDate?): String? {
    return date?.format(formatter)
  }
}
