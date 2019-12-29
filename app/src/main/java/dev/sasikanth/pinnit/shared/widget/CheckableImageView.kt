package dev.sasikanth.pinnit.shared.widget

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
import androidx.core.view.accessibility.AccessibilityEventCompat
import androidx.customview.view.AbsSavedState

class CheckableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), Checkable {

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
    val savedState = state
    super.onRestoreInstanceState(savedState.superState)
    isChecked = savedState.checked
  }

  /** Sets image button to be checkable or not.  */
  fun setCheckable(checkable: Boolean) {
    if (this.checkable != checkable) {
      this.checkable = checkable
      sendAccessibilityEvent(AccessibilityEventCompat.CONTENT_CHANGE_TYPE_UNDEFINED)
    }
  }

  /** Returns whether the image button is checkable.  */
  fun isCheckable(): Boolean {
    return checkable
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

    companion object {
      @JvmField
      val CREATOR: Creator<SavedState> = object : ClassLoaderCreator<SavedState> {
        override fun createFromParcel(
            `in`: Parcel,
            loader: ClassLoader
        ): SavedState {
          return SavedState(`in`, loader)
        }

        override fun createFromParcel(`in`: Parcel): SavedState {
          return SavedState(`in`, null)
        }

        override fun newArray(size: Int): Array<SavedState?> {
          return arrayOfNulls(size)
        }
      }
    }
  }
}
