package dev.sasikanth.pinnit.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.sasikanth.pinnit.utils.inTransaction

@Suppress("ClassName")
object Migration_1_2 : Migration(1, 2) {

  override fun migrate(database: SupportSQLiteDatabase) {
    with(database) {
      inTransaction {
        execSQL(""" ALTER TABLE PinnitNotification ADD COLUMN scheduleDate TEXT DEFAULT NULL """)
        execSQL(""" ALTER TABLE PinnitNotification ADD COLUMN scheduleTime TEXT DEFAULT NULL """)
        execSQL(""" ALTER TABLE PinnitNotification ADD COLUMN scheduleType TEXT DEFAULT NULL """)
      }
    }
  }
}
