package dev.sasikanth.pinnit.shared.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.button.MaterialButton
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.shared.doOnApplyWindowInsets

class PinnitBottomBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), CoordinatorLayout.AttachedBehavior {

    private val actionSearch: ImageView
    private val actionOptions: ImageView
    private val tooltip: AppCompatTextView
    private val pageAction: MaterialButton

    init {
        inflate(context, R.layout.pinnit_bottom_bar, this)

        actionSearch = findViewById(R.id.pinnit_search)
        actionOptions = findViewById(R.id.pinnit_options)
        tooltip = findViewById(R.id.tooltip)
        pageAction = findViewById(R.id.pinnit_page_action)

        val bottomAppBarContent: ConstraintLayout = findViewById(R.id.bottom_app_bar_content)
        bottomAppBarContent.doOnApplyWindowInsets { view, windowInsets, padding, _ ->
            val bottom = windowInsets.systemWindowInsetBottom
            val left = windowInsets.systemWindowInsetLeft
            val right = windowInsets.systemWindowInsetRight

            view.setPadding(
                padding.left + left,
                padding.top + 0,
                padding.right + right,
                padding.bottom + bottom
            )
        }
    }

    fun isPageActionEnabled(isEnabled: Boolean) {
        pageAction.isEnabled = isEnabled
    }

    fun isTooltipVisible(visible: Boolean) {
        tooltip.isVisible = visible
        pageAction.isVisible = !visible
        actionSearch.isVisible = !visible
    }

    fun setPageActionButtonTitle(@StringRes stringRes: Int) {
        pageAction.text = context.getString(stringRes)
    }

    fun setOnPageActionClick(onClickListener: OnClickListener) {
        pageAction.setOnClickListener(onClickListener)
    }

    fun setOnSearchClick(onClickListener: OnClickListener) {
        actionSearch.setOnClickListener(onClickListener)
    }

    fun setOnOptionsClick(onClickListener: OnClickListener) {
        actionOptions.setOnClickListener(onClickListener)
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return HideBottomViewOnScrollBehavior<PinnitBottomBar>()
    }
}
