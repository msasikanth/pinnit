package dev.sasikanth.pinnit.utils

import android.view.View
import android.view.WindowInsets

fun View.donOnApplyWindowInsets(f: (View, WindowInsets, InitialPadding) -> Unit) {
  // Create a snapshot of the view's padding state
  val initialPadding = recordInitialPadding(this)
  // Set an actual OnApplyWindowInsetsListener which proxies to the given
  // lambda, also passing in the original padding state
  setOnApplyWindowInsetsListener { v, insets ->
    f(v, insets, initialPadding)
    // Always return the insets, so that children can also use them
    insets
  }
  // request some insets
  requestApplyInsets()
}

data class InitialPadding(
  val left: Int,
  val top: Int,
  val right: Int,
  val bottom: Int
)

private fun recordInitialPadding(view: View) = InitialPadding(
  view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)
