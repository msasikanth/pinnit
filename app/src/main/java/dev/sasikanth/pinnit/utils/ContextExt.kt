package dev.sasikanth.pinnit.utils

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

// Source: https://github.com/afollestad/material-dialogs/blob/66582d9993bcf55bfea873b4bf484a429ce3df36/core/src/main/java/com/afollestad/materialdialogs/utils/MDUtil.kt#L103
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
