package dev.sasikanth.pinnit.data

import android.graphics.drawable.Drawable

data class AppItem(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val isSelected: Boolean = false
) {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AppItem

    if (packageName != other.packageName) return false
    if (appName != other.appName) return false
    if (isSelected != other.isSelected) return false

    return true
  }

  override fun hashCode(): Int {
    var result = packageName.hashCode()
    result = 31 * result + appName.hashCode()
    result = 31 * result + isSelected.hashCode()
    return result
  }
}
