package dev.sasikanth.pinnit.utils.room

import androidx.room.TypeConverter
import java.time.Instant

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