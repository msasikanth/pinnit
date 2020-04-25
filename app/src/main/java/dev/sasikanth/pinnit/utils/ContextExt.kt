package dev.sasikanth.pinnit.utils

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

@ColorInt
fun Context.resolveColor(
  @ColorRes colorRes: Int? = null,
  @AttrRes attrRes: Int? = null,
  fallback: (() -> Int)? = null
): Int {
  if (attrRes != null) {
    val a = theme.obtainStyledAttributes(intArrayOf(attrRes))
    try {
      val result = a.getColor(0, 0)
      if (result == 0 && fallback != null) {
        return fallback()
      }
      return result
    } finally {
      a.recycle()
    }
  }
  return ContextCompat.getColor(this, colorRes ?: 0)
}
