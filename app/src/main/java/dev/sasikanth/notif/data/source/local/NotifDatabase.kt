package dev.sasikanth.notif.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.sasikanth.notif.data.NotifItem

@Database(entities = [NotifItem::class], version = 1)
abstract class NotifDatabase : RoomDatabase() {

    abstract fun notifDao(): NotifDao
}
