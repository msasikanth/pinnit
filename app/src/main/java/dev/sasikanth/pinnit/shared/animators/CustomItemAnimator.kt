package dev.sasikanth.pinnit.shared.animators

import android.view.animation.AccelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealCompat
import dev.sasikanth.pinnit.pages.historynotifs.HistoryAdapter.PinnitItemViewHolder
import kotlinx.android.synthetic.main.pinnit_item.*
import kotlin.math.hypot

class CustomItemAnimator : DefaultItemAnimator() {

  private val accelerateInterpolator = AccelerateInterpolator()
  private val fastOutSlowInInterpolator = FastOutSlowInInterpolator()

  override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
    return true
  }

  private fun getItemHolderInfo(
      pinnitItemViewHolder: PinnitItemViewHolder,
      info: NotifItemInfo
  ): ItemHolderInfo {
    val pinnitItem = pinnitItemViewHolder.pinnitItem
    info.key = pinnitItem.key
    info.isPinned = pinnitItem.isPinned
    return info
  }

  override fun obtainHolderInfo(): ItemHolderInfo {
    return NotifItemInfo()
  }

  override fun recordPreLayoutInformation(
      state: RecyclerView.State,
      viewHolder: RecyclerView.ViewHolder,
      changeFlags: Int,
      payloads: MutableList<Any>
  ): ItemHolderInfo {
    val notifItemInfo = super.recordPreLayoutInformation(
        state,
        viewHolder,
        changeFlags,
        payloads
    ) as NotifItemInfo
    return getItemHolderInfo(viewHolder as PinnitItemViewHolder, notifItemInfo)
  }

  override fun recordPostLayoutInformation(
      state: RecyclerView.State,
      viewHolder: RecyclerView.ViewHolder
  ): ItemHolderInfo {
    val notifItemInfo = super.recordPostLayoutInformation(state, viewHolder) as NotifItemInfo
    return getItemHolderInfo(viewHolder as PinnitItemViewHolder, notifItemInfo)
  }

  override fun animateChange(
      oldHolder: RecyclerView.ViewHolder,
      newHolder: RecyclerView.ViewHolder,
      preInfo: ItemHolderInfo,
      postInfo: ItemHolderInfo
  ): Boolean {
    if (newHolder != oldHolder) {
      return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }

    return if ((newHolder as? PinnitItemViewHolder != null) &&
        (oldHolder as? PinnitItemViewHolder != null) &&
        (preInfo as? NotifItemInfo != null) &&
        (postInfo as? NotifItemInfo != null)
    ) {
      val oldPinStatus = preInfo.isPinned
      val newPinStatus = postInfo.isPinned

      if (oldPinStatus == newPinStatus || preInfo.key != postInfo.key) {
        super.animateChange(oldHolder, newHolder, preInfo, postInfo)
      }

      try {
        val itemView = newHolder.itemView

        val cx = newHolder.getRevealCx()
        val cy = newHolder.getRevealCy()

        val viewWidth = itemView.width.toFloat()
        val viewHeight = itemView.height.toFloat()
        val viewRadius = hypot(viewWidth, viewHeight)

        val anim = if (newPinStatus) {
          CircularRevealCompat.createCircularReveal(
              newHolder.pinnedRevealLayout,
              cx,
              cy,
              0.0f,
              viewRadius
          )
        } else {
          CircularRevealCompat.createCircularReveal(
              newHolder.pinnedRevealLayout,
              cx,
              cy,
              viewRadius,
              0.0f
          )
        }

        newHolder.pinnedRevealLayout.isVisible = true

        if (newPinStatus) {
          anim.duration = 250
          anim.interpolator = fastOutSlowInInterpolator
          anim.addListener(
              onStart = {
                newHolder.apply {
                  toggleNotificationPin.isChecked = true
                }
              },
              onEnd = {
                newHolder.apply {
                  newHolder.updateColorsBasedOnPinStatus()
                  dispatchAnimationFinished(this)
                }
              }
          )
        } else {
          anim.duration = 200
          anim.interpolator = accelerateInterpolator
          anim.addListener(
              onEnd = {
                newHolder.apply {
                  pinnedRevealLayout.isVisible = false
                  toggleNotificationPin.isChecked = false
                  newHolder.updateColorsBasedOnPinStatus()
                  dispatchAnimationFinished(this)
                }
              }
          )
        }

        anim.start()
        true
      } catch (e: Exception) {
        super.animateChange(oldHolder, newHolder, preInfo, postInfo)
      }
    } else {
      super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }
  }

  class NotifItemInfo : ItemHolderInfo() {
    var key: Long = 0L
    var isPinned: Boolean = false
  }
}
