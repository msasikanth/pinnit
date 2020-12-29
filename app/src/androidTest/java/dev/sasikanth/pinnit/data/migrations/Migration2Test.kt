package dev.sasikanth.pinnit.data.migrations

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.sasikanth.pinnit.assertValues
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.insert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.Instant
import java.util.UUID

private const val TEST_DB = "pinnit-test-db"

@RunWith(AndroidJUnit4::class)
class Migration2Test {

  @get:Rule
  val helper: MigrationTestHelper = MigrationTestHelper(
    InstrumentationRegistry.getInstrumentation(),
    AppDatabase::class.java.canonicalName,
    FrameworkSQLiteOpenHelperFactory()
  )

  @Test
  @Throws(IOException::class)
  fun migrate1To2() {
    // given
    val notificationId = UUID.fromString("4179c32e-3483-4d45-962d-0e58d3b4d960")
    var db = helper.createDatabase(TEST_DB, 1)

    db.insert(
      "PinnitNotification", mapOf(
        "uuid" to notificationId,
        "title" to "Sample Title",
        "content" to "Sample Content",
        "isPinned" to true,
        "createdAt" to Instant.parse("2020-01-01T00:00:00.00Z"),
        "updatedAt" to Instant.parse("2020-01-01T00:00:00.00Z"),
        "deletedAt" to null
      )
    )

    // Prepare for the next version.
    db.close()

    // when
    db = helper.runMigrationsAndValidate(TEST_DB, 2, true, Migration_1_2)

    // then
    db.query(""" SELECT * FROM PinnitNotification """).use { cursor ->
      cursor.moveToFirst()
      cursor.assertValues(
        mapOf(
          "uuid" to notificationId,
          "title" to "Sample Title",
          "content" to "Sample Content",
          "isPinned" to true,
          "createdAt" to Instant.parse("2020-01-01T00:00:00.00Z"),
          "updatedAt" to Instant.parse("2020-01-01T00:00:00.00Z"),
          "deletedAt" to null,
          "scheduleDate" to null,
          "scheduleTime" to null,
          "scheduleType" to null
        )
      )
    }
  }
}
