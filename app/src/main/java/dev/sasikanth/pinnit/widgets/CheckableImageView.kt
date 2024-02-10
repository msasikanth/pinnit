package dev.sasikanth.pinnit.widgets

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView
import androidx.customview.view.AbsSavedState

/**
 * [AppCompatImageView] that implements [Checkable] interface to support
 * setting whether or not image is checked.
 */
class CheckableImageView(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs), Checkable {
  private val checkStateSet = intArrayOf(android.R.attr.state_checked)
  private var checked = false
  private var checkable = true

  override fun setChecked(checked: Boolean) {
    if (checkable && this.checked != checked) {
      this.checked = checked
      refreshDrawableState()
      sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
    }
  }

  override fun isChecked(): Boolean = checked

  override fun toggle() {
    isChecked = !checked
  }

  override fun onCreateDrawableState(extraSpace: Int): IntArray? {
    return if (checked) {
      View.mergeDrawableStates(
        super.onCreateDrawableState(extraSpace + checkStateSet.size),
        checkStateSet
      )
    } else {
      super.onCreateDrawableState(extraSpace)
    }
  }

  override fun onSaveInstanceState(): Parcelable {
    val superState = super.onSaveInstanceState()
    val savedState = SavedState(superState)
    savedState.checked = checked
    return savedState
  }

  override fun onRestoreInstanceState(state: Parcelable?) {
    if (state !is SavedState) {
      super.onRestoreInstanceState(state)
      return
    }
    super.onRestoreInstanceState(state.superState)
    isChecked = state.checked
  }

  class SavedState : AbsSavedState {
    constructor(superState: Parcelable?) : super(superState!!)
    constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
      readFromParcel(source)
    }

    internal var checked = false

    override fun writeToParcel(out: Parcel, flags: Int) {
      super.writeToParcel(out, flags)
      out.writeInt(if (checked) 1 else 0)
    }

    private fun readFromParcel(`in`: Parcel) {
      checked = `in`.readInt() == 1
    }
  }
}
