package dev.sasikanth.notif.shared

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources

val Int.px: Int
    get() = (Resources.getSystem().displayMetrics.density * this).toInt()

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Context.isNightMode: Boolean
    get() {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }
