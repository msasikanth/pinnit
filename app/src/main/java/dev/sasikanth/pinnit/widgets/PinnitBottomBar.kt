package dev.sasikanth.pinnit.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import androidx.core.view.isGone
import androidx.core.view.isVisible
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.databinding.PinnitBottomBarBinding
import dev.sasikanth.pinnit.utils.dp

class PinnitBottomBar @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

  private var _binding: PinnitBottomBarBinding? = null
  private val binding get() = _binding!!

  init {
    val layoutInflater = LayoutInflater.from(context)
    _binding = PinnitBottomBarBinding.inflate(layoutInflater, this)

    context.theme.obtainStyledAttributes(attrs, R.styleable.PinnitBottomBar, defStyleAttr, 0).use { typedArray ->
      val mNavigationIcon = typedArray.getResourceIdOrThrow(R.styleable.PinnitBottomBar_navigationIcon)
      val mContentActionText = typedArray.getResourceIdOrThrow(R.styleable.PinnitBottomBar_contentActionText)
      val mActionIcon = typedArray.getResourceIdOrThrow(R.styleable.PinnitBottomBar_actionIcon)

      setNavigationIcon(mNavigationIcon)
      setContentActionText(mContentActionText)
      setActionIcon(mActionIcon)
    }

    // Adding Z-axis so that the snackbar will start
    // coming up behind the bottom bar
    translationZ = 4.dp.toFloat()
    applySystemWindowInsetsToPadding(bottom = true, left = true, right = true)
  }

  override fun onDetachedFromWindow() {
    _binding = null
    super.onDetachedFromWindow()
  }

  fun setNavigationIcon(@DrawableRes navigationIcon: Int?) {
    if (navigationIcon != null) {
      binding.navigationIcon.isVisible = true
      binding.navigationIcon.setImageResource(navigationIcon)
    } else {
      binding.navigationIcon.isGone = true
    }
  }

  fun setContentActionText(@StringRes contentActionTextRes: Int?) {
    if (contentActionTextRes != null) {
      binding.contentActionButton.isVisible = true
      binding.contentActionButton.setText(contentActionTextRes)
    } else {
      binding.contentActionButton.isGone = true
    }
  }

  fun setContentActionText(contentActionText: String?) {
    if (contentActionText != null) {
      binding.contentActionButton.isVisible = true
      binding.contentActionButton.text = contentActionText
    } else {
      binding.contentActionButton.isGone = true
    }
  }

  fun setActionIcon(@DrawableRes actionIcon: Int?) {
    if (actionIcon != null) {
      binding.actionIcon.isVisible = true
      binding.actionIcon.setImageResource(actionIcon)
    } else {
      binding.actionIcon.isGone = true
    }
  }

  fun setContentActionEnabled(isEnabled: Boolean) {
    binding.contentActionButton.isEnabled = isEnabled
  }

  fun setNavigationOnClickListener(listener: ((View) -> Unit)?) {
    binding.navigationIcon.setOnClickListener(listener)
  }

  fun setContentActionOnClickListener(listener: ((View) -> Unit)?) {
    binding.contentActionButton.setOnClickListener(listener)
  }

  fun setActionOnClickListener(listener: ((View) -> Unit)?) {
    binding.actionIcon.setOnClickListener(listener)
  }
}
