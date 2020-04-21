package dev.sasikanth.pinnit.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getResourceIdOrThrow
import dev.sasikanth.pinnit.R
import kotlinx.android.synthetic.main.pinnit_bottom_bar.view.*

class PinnitBottomBar @JvmOverloads
constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

  @DrawableRes
  private var mNavigationIcon: Int = 0

  @StringRes
  private var mContentActionText: Int = 0

  @DrawableRes
  private var mActionIcon: Int = 0

  init {
    inflate(context, R.layout.pinnit_bottom_bar, this)

    context.theme.obtainStyledAttributes(attrs, R.styleable.PinnitBottomBar, defStyleAttr, 0).apply {
      mNavigationIcon = getResourceIdOrThrow(R.styleable.PinnitBottomBar_navigationIcon)
      mContentActionText = getResourceIdOrThrow(R.styleable.PinnitBottomBar_contentActionText)
      mActionIcon = getResourceIdOrThrow(R.styleable.PinnitBottomBar_actionIcon)
      recycle()
    }
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    if (isInEditMode) return

    navigationIcon.setImageResource(mNavigationIcon)
    contentActionButton.setText(mContentActionText)
    actionIcon.setImageResource(mActionIcon)
  }

  fun setNavigationIcon(@DrawableRes navigationIcon: Int) {
    mNavigationIcon = navigationIcon
    invalidate()
    requestLayout()
  }

  fun setContentActionText(@StringRes contentActionText: Int) {
    mContentActionText = contentActionText
    invalidate()
    requestLayout()
  }

  fun setActionIcon(@DrawableRes actionIcon: Int) {
    mActionIcon = actionIcon
    invalidate()
    requestLayout()
  }
}
