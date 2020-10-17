package dev.sasikanth.pinnit.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sasikanth.pinnit.utils.room.InstantRoomTypeConverter
import dev.sasikanth.pinnit.utils.room.LocalDateRoomTypeConverter
import dev.sasikanth.pinnit.utils.room.LocalTimeRoomTypeConverter
import dev.sasikanth.pinnit.utils.room.UuidRoomTypeConverter

@Database(
  entities = [PinnitNotification::class],
  version = 2
)
@TypeConverters(
  UuidRoomTypeConverter::class,
  InstantRoomTypeConverter::class,
  ScheduleTypeConverter::class,
  LocalDateRoomTypeConverter::class,
  LocalTimeRoomTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun notificationDao(): PinnitNotification.RoomDao
}
