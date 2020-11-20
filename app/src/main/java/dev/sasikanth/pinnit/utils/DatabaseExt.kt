package dev.sasikanth.pinnit.utils

import androidx.sqlite.db.SupportSQLiteDatabase

fun SupportSQLiteDatabase.inTransaction(block: SupportSQLiteDatabase.() -> Unit) {
  beginTransaction()
  try {
    block.invoke(this)
    setTransactionSuccessful()
  } finally {
    endTransaction()
  }
}
