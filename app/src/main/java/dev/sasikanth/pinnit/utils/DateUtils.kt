package dev.sasikanth.pinnit.utils

import android.text.format.DateUtils as AndroidDateUtils
import java.util.Calendar

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
