package dev.sasikanth.pinnit

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.common.truth.Truth
import dev.sasikanth.pinnit.utils.room.LocalDateRoomTypeConverter
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

private val dateConverter = LocalDateRoomTypeConverter()

fun Cursor.string(column: String): String? = getString(getColumnIndex(column))
fun Cursor.boolean(column: String): Boolean? = getInt(getColumnIndex(column)) == 1
fun Cursor.integer(columnName: String): Int? = getInt(getColumnIndex(columnName))
fun Cursor.long(columnName: String): Long = getLong(getColumnIndex(columnName))
fun Cursor.double(columnName: String): Double = getDouble(getColumnIndex(columnName))
fun Cursor.float(columnName: String): Float = getFloat(getColumnIndex(columnName))
fun Cursor.uuid(columnName: String): UUID? = string(columnName)?.let { UUID.fromString(it) }
fun Cursor.instant(columnName: String): Instant? = string(columnName)?.let { Instant.parse(it) }
fun Cursor.localDate(columnName: String): LocalDate? = string(columnName).let(dateConverter::toLocalDate)

fun SupportSQLiteDatabase.insert(tableName: String, valuesMap: Map<String, Any?>) {
  val contentValues = valuesMap
    .entries
    .fold(ContentValues()) { values, (key, value) ->
      when (value) {
        null -> values.putNull(key)
        is Int -> values.put(key, value)
        is Long -> values.put(key, value)
        is Float -> values.put(key, value)
        is Double -> values.put(key, value)
        is Boolean -> values.put(key, value)
        is String -> values.put(key, value)
        is UUID -> values.put(key, value.toString())
        is Instant -> values.put(key, value.toString())
        is LocalDate -> values.put(key, dateConverter.fromLocalDate(value))
        else -> throw IllegalArgumentException("Unknown type (${value.javaClass.name}) for key: $key")
      }

      values
    }

  insert(tableName, SQLiteDatabase.CONFLICT_ABORT, contentValues)
}

fun Cursor.assertValues(valuesMap: Map<String, Any?>) {
  Truth.assertThat(columnNames.toSet()).containsExactlyElementsIn(valuesMap.keys)
  valuesMap
    .forEach { (key, value) ->
      val withMessage = Truth.assertWithMessage("For column [$key]: ")
      when (value) {
        null -> withMessage.that(isNull(getColumnIndex(key))).isTrue()
        is Int -> withMessage.that(integer(key)).isEqualTo(value)
        is Long -> withMessage.that(long(key)).isEqualTo(value)
        is Float -> withMessage.that(float(key)).isEqualTo(value)
        is Double -> withMessage.that(double(key)).isEqualTo(value)
        is Boolean -> withMessage.that(boolean(key)).isEqualTo(value)
        is String -> withMessage.that(string(key)).isEqualTo(value)
        is UUID -> withMessage.that(uuid(key)).isEqualTo(value)
        is Instant -> withMessage.that(instant(key)).isEqualTo(value)
        is LocalDate -> withMessage.that(localDate(key)).isEqualTo(value)
        else -> throw IllegalArgumentException("Unknown type (${value.javaClass.name}) for key: $key")
      }
    }
}
