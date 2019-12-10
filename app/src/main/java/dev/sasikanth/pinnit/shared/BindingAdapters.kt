package dev.sasikanth.pinnit.shared

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageView
import com.google.android.material.textview.MaterialTextView
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import coil.api.load
import coil.transform.CircleCropTransformation
import de.hdodenhof.circleimageview.CircleImageView
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.TemplateStyle

@BindingAdapter("notifIcon")
fun CircleImageView.setNotifIcon(pinnitItem: PinnitItem?) {
    pinnitItem?.let {
        val notifIconUri = it.notifIcon
        if (notifIconUri != null) {
            val colorDrawable = ColorDrawable(context.resolveColor(attrRes = R.attr.colorPrimary))
            load(pinnitItem.notifIcon) {
                transformations(CircleCropTransformation())
                placeholder(colorDrawable)
                error(colorDrawable)
            }
        }
    }
}

@BindingAdapter("notifTitle")
fun MaterialTextView.setNotifTitle(pinnitItem: PinnitItem?) {
    pinnitItem?.let {
        text = pinnitItem.title
    }
}

@BindingAdapter("notifText")
fun MaterialTextView.setNotifText(pinnitItem: PinnitItem?) {
    pinnitItem?.let { pinnit ->
        if (pinnit.template == TemplateStyle.MessagingStyle) {
            val messagesBuilder = StringBuilder()
            val lastMessages = pinnit.messages.takeLast(5).sortedBy { it.timestamp }

            lastMessages.forEachIndexed { index, message ->
                val messageText = "${message.senderName}: ${message.message}"
                if (index == lastMessages.lastIndex) {
                    messagesBuilder.append(messageText)
                } else {
                    messagesBuilder.appendln(messageText)
                }
            }

            text = messagesBuilder.toString()
        } else {
            text = pinnit.text
        }
    }
}

@BindingAdapter("srcRes")
fun ImageView.setImageRes(drawableRes: Int) {
    setImageResource(drawableRes)
}

@BindingAdapter("isVisible")
fun View.setVisibility(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("isOptionSelected")
fun View.setOptionSelected(isSelected: Boolean) {
    if (isSelected) {
        this.setBackgroundResource(R.drawable.option_item_selected)
    } else {
        this.background = null
    }
}

@BindingAdapter("layoutFullscreen")
fun View.bindLayoutFullscreen(previousFullscreen: Boolean, fullscreen: Boolean) {
    if (previousFullscreen != fullscreen && fullscreen) {
        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
}

@BindingAdapter(
    "paddingLeftSystemWindowInsets",
    "paddingTopSystemWindowInsets",
    "paddingRightSystemWindowInsets",
    "paddingBottomSystemWindowInsets",
    requireAll = false
)
fun View.applySystemWindowInsetsPadding(
    previousApplyLeft: Boolean,
    previousApplyTop: Boolean,
    previousApplyRight: Boolean,
    previousApplyBottom: Boolean,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
        previousApplyTop == applyTop &&
        previousApplyRight == applyRight &&
        previousApplyBottom == applyBottom
    ) {
        return
    }

    doOnApplyWindowInsets { view, insets, padding, _ ->
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

        view.setPadding(
            padding.left + left,
            padding.top + top,
            padding.right + right,
            padding.bottom + bottom
        )
    }
}

@BindingAdapter(
    "marginLeftSystemWindowInsets",
    "marginTopSystemWindowInsets",
    "marginRightSystemWindowInsets",
    "marginBottomSystemWindowInsets",
    requireAll = false
)
fun View.applySystemWindowInsetsMargin(
    previousApplyLeft: Boolean,
    previousApplyTop: Boolean,
    previousApplyRight: Boolean,
    previousApplyBottom: Boolean,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    if (previousApplyLeft == applyLeft &&
        previousApplyTop == applyTop &&
        previousApplyRight == applyRight &&
        previousApplyBottom == applyBottom
    ) {
        return
    }

    doOnApplyWindowInsets { view, insets, _, margin ->
        val left = if (applyLeft) insets.systemWindowInsetLeft else 0
        val top = if (applyTop) insets.systemWindowInsetTop else 0
        val right = if (applyRight) insets.systemWindowInsetRight else 0
        val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            leftMargin = margin.left + left
            topMargin = margin.top + top
            rightMargin = margin.right + right
            bottomMargin = margin.bottom + bottom
        }
    }
}

fun View.doOnApplyWindowInsets(block: (View, WindowInsets, InitialPadding, InitialMargin) -> Unit) {
    // Create a snapshot of the view's padding & margin states
    val initialPadding = recordInitialPaddingForView(this)
    val initialMargin = recordInitialMarginForView(this)
    // Set an actual OnApplyWindowInsetsListener which proxies to the given
    // lambda, also passing in the original padding & margin states
    setOnApplyWindowInsetsListener { v, insets ->
        block(v, insets, initialPadding, initialMargin)
        // Always return the insets, so that children can also use them
        insets
    }
    // request some insets
    requestApplyInsetsWhenAttached()
}

class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialMarginForView(view: View): InitialMargin {
    val lp = view.layoutParams as? ViewGroup.MarginLayoutParams
        ?: throw IllegalArgumentException("Invalid view layout params")
    return InitialMargin(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
}

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        // We're already attached, just request as normal
        requestApplyInsets()
    } else {
        // We're not attached to the hierarchy, add a listener to
        // request when we are
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}
