package dev.sasikanth.pinnit.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sasikanth.pinnit.data.NotifItem
import dev.sasikanth.pinnit.data.converters.MessageStyleConverter
import dev.sasikanth.pinnit.data.converters.TemplateStyleConverter

@TypeConverters(TemplateStyleConverter::class, MessageStyleConverter::class)
@Database(entities = [NotifItem::class], version = 1)
abstract class NotifDatabase : RoomDatabase() {

    abstract val notifDao: NotifDao
}
