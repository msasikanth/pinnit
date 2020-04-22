package dev.sasikanth.pinnit.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sasikanth.pinnit.utils.room.InstantRoomTypeConverter
import dev.sasikanth.pinnit.utils.room.UuidRoomTypeConverter

@Database(
  entities = [PinnitNotification::class],
  version = 1
)
@TypeConverters(
  UuidRoomTypeConverter::class,
  InstantRoomTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun notificationDao(): PinnitNotification.RoomDao
}
