package dev.sasikanth.pinnit.options

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class OptionItem(
    @IdRes val id: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val isSelected: Boolean = false
)
