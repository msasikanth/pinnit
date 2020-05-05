package dev.sasikanth.pinnit.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.view.isGone
import androidx.core.view.isVisible
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.R
import kotlinx.android.synthetic.main.pinnit_bottom_bar.view.*

class PinnitBottomBar @JvmOverloads
constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

  init {
    inflate(context, R.layout.pinnit_bottom_bar, this)

    context.theme.obtainStyledAttributes(attrs, R.styleable.PinnitBottomBar, defStyleAttr, 0).apply {
      val mNavigationIcon = getResourceIdOrThrow(R.styleable.PinnitBottomBar_navigationIcon)
      val mContentActionText = getResourceIdOrThrow(R.styleable.PinnitBottomBar_contentActionText)
      val mActionIcon = getResourceIdOrThrow(R.styleable.PinnitBottomBar_actionIcon)

      setNavigationIcon(mNavigationIcon)
      setContentActionText(mContentActionText)
      setActionIcon(mActionIcon)

      recycle()
    }

    applySystemWindowInsetsToPadding(bottom = true, left = true, right = true)
  }

  fun setNavigationIcon(@DrawableRes navigationIcon: Int?) {
    if (navigationIcon != null) {
      this.navigationIcon.isVisible = true
      this.navigationIcon.setImageResource(navigationIcon)
    } else {
      this.navigationIcon.isGone = true
    }
  }

  fun setContentActionText(@StringRes contentActionTextRes: Int?) {
    if (contentActionTextRes != null) {
      this.contentActionButton.isVisible = true
      this.contentActionButton.setText(contentActionTextRes)
    } else {
      this.contentActionButton.isGone = true
    }
  }

  fun setContentActionText(contentActionText: String?) {
    if (contentActionText != null) {
      this.contentActionButton.isVisible = true
      this.contentActionButton.text = contentActionText
    } else {
      this.contentActionButton.isGone = true
    }
  }

  fun setActionIcon(@DrawableRes actionIcon: Int?) {
    if (actionIcon != null) {
      this.actionIcon.isVisible = true
      this.actionIcon.setImageResource(actionIcon)
    } else {
      this.actionIcon.isGone = true
    }
  }

  fun setContentActionEnabled(isEnabled: Boolean) {
    contentActionButton.isEnabled = isEnabled
  }

  fun setNavigationOnClickListener(listener: ((View) -> Unit)?) {
    navigationIcon.setOnClickListener(listener)
  }

  fun setContentActionOnClickListener(listener: ((View) -> Unit)?) {
    contentActionButton.setOnClickListener(listener)
  }

  fun setActionOnClickListener(listener: ((View) -> Unit)?) {
    actionIcon.setOnClickListener(listener)
  }
}
