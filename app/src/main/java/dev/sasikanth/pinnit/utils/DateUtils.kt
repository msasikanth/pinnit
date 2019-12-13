package dev.sasikanth.pinnit.utils

import java.util.Calendar
import android.text.format.DateUtils as AndroidDateUtils

object DateUtils {

  @JvmStatic
  fun getRelativeTime(postedOn: Long): String {
    val calendar = Calendar.getInstance()
    return AndroidDateUtils.getRelativeTimeSpanString(
        postedOn,
        calendar.timeInMillis,
        AndroidDateUtils.SECOND_IN_MILLIS
    ).toString()
  }
}
