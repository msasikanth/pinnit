package dev.sasikanth.notif.data

import android.graphics.drawable.Drawable

data class AppItem(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val isSelected: Boolean = false
)
