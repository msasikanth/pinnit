package dev.sasikanth.pinnit.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.sasikanth.pinnit.data.NotifItem
import dev.sasikanth.pinnit.data.converters.MessageStyleConverter
import dev.sasikanth.pinnit.data.converters.TemplateStyleConverter

@TypeConverters(TemplateStyleConverter::class, MessageStyleConverter::class)
@Database(entities = [NotifItem::class], version = 2)
abstract class NotifDatabase : RoomDatabase() {

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE notifs ADD COLUMN is_current INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        fun createDatabase(context: Context): NotifDatabase {
            return Room.databaseBuilder(
                context,
                NotifDatabase::class.java,
                "Notifs.db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }

    abstract val notifDao: NotifDao
}
