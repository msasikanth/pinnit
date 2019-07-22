package dev.sasikanth.notif.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sasikanth.notif.data.NotifItem
import dev.sasikanth.notif.data.converters.MessageStyleConverter
import dev.sasikanth.notif.data.converters.TemplateStyleConverter

@TypeConverters(TemplateStyleConverter::class, MessageStyleConverter::class)
@Database(entities = [NotifItem::class], version = 1)
abstract class NotifDatabase : RoomDatabase() {

    abstract fun notifDao(): NotifDao
}
