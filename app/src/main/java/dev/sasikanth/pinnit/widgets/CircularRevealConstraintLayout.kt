package dev.sasikanth.pinnit.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.circularreveal.CircularRevealHelper
import com.google.android.material.circularreveal.CircularRevealWidget

class CircularRevealConstraintLayout constructor(
  context: Context,
  attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs), CircularRevealWidget {

  private val helper = CircularRevealHelper(this)

  override fun getRevealInfo(): CircularRevealWidget.RevealInfo? = helper.revealInfo

  override fun setRevealInfo(revealInfo: CircularRevealWidget.RevealInfo?) {
    helper.revealInfo = revealInfo
  }

  override fun getCircularRevealOverlayDrawable(): Drawable? {
    return helper.circularRevealOverlayDrawable
  }

  override fun getCircularRevealScrimColor(): Int {
    return helper.circularRevealScrimColor
  }

  override fun setCircularRevealScrimColor(color: Int) {
    helper.circularRevealScrimColor = color
  }

  override fun setCircularRevealOverlayDrawable(drawable: Drawable?) {
    helper.circularRevealOverlayDrawable = drawable
  }

  override fun buildCircularRevealCache() {
    helper.buildCircularRevealCache()
  }

  override fun actualIsOpaque(): Boolean {
    return helper.isOpaque
  }

  @SuppressLint("MissingSuperCall")
  override fun draw(canvas: Canvas) {
    helper.draw(canvas)
  }

  override fun actualDraw(canvas: Canvas?) {
    super.draw(canvas)
  }

  override fun destroyCircularRevealCache() {
    helper.destroyCircularRevealCache()
  }
}
