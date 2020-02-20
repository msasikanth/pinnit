package dev.sasikanth.pinnit.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.converters.MessageStyleConverter
import dev.sasikanth.pinnit.data.converters.TemplateStyleConverter
import dev.sasikanth.pinnit.data.converters.UriTypeConverter

@TypeConverters(
    TemplateStyleConverter::class,
    MessageStyleConverter::class,
    UriTypeConverter::class
)
@Database(entities = [PinnitItem::class], version = 1)
abstract class PinnitDatabase : RoomDatabase() {

  companion object {
    fun createDatabase(context: Context): PinnitDatabase {
      return Room.databaseBuilder(
          context,
          PinnitDatabase::class.java,
          "pinnit.db"
      ).fallbackToDestructiveMigration()
          .build()
    }
  }

  abstract val pinnitDao: PinnitDao
}
