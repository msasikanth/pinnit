package dev.sasikanth.pinnit.utils

import android.content.res.Resources

val Int.px: Int
  get() = (Resources.getSystem().displayMetrics.density * this).toInt()

val Int.dp: Int
  get() = (this / Resources.getSystem().displayMetrics.density).toInt()

