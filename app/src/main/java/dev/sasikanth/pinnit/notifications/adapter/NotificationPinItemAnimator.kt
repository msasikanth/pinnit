package dev.sasikanth.pinnit.notifications.adapter

import android.view.animation.AccelerateInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.pinnit.notifications.adapter.NotificationsListAdapter.NotificationViewHolder
import java.util.UUID

class NotificationPinItemAnimator : DefaultItemAnimator() {

  private val accelerateInterpolator = AccelerateInterpolator()
  private val fastOutSlowInInterpolator = FastOutSlowInInterpolator()

  override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
    return true
  }

  private fun getItemHolderInfo(
    viewHolder: RecyclerView.ViewHolder,
    info: NotificationItemInfo
  ): ItemHolderInfo {
    val notification = (viewHolder as NotificationViewHolder).notification
    if (notification != null) {
      info.uuid = notification.uuid
      info.isPinned = notification.isPinned
    }
    return info
  }

  override fun obtainHolderInfo(): ItemHolderInfo {
    return NotificationItemInfo()
  }

  override fun recordPreLayoutInformation(
    state: RecyclerView.State,
    viewHolder: RecyclerView.ViewHolder,
    changeFlags: Int,
    payloads: MutableList<Any>
  ): ItemHolderInfo {
    val notificationItemInfo = super.recordPreLayoutInformation(
      state,
      viewHolder,
      changeFlags,
      payloads
    ) as NotificationItemInfo
    return getItemHolderInfo(viewHolder, notificationItemInfo)
  }

  override fun recordPostLayoutInformation(
    state: RecyclerView.State,
    viewHolder: RecyclerView.ViewHolder
  ): ItemHolderInfo {
    val notificationItemInfo = super.recordPostLayoutInformation(state, viewHolder) as NotificationItemInfo
    return getItemHolderInfo(viewHolder, notificationItemInfo)
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

    if ((newHolder as? NotificationViewHolder != null) &&
      (oldHolder as? NotificationViewHolder != null) &&
      (preInfo as? NotificationItemInfo != null) &&
      (postInfo as? NotificationItemInfo != null)
    ) {
      val oldPinStatus = preInfo.isPinned
      val newPinStatus = postInfo.isPinned

      if (oldPinStatus == newPinStatus || preInfo.uuid != postInfo.uuid) {
        return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
      }

      return try {
        newHolder.animateReveal(
          newPinStatus = newPinStatus,
          fastOutSlowInInterpolator = fastOutSlowInInterpolator,
          accelerateInterpolator = accelerateInterpolator,
          dispatchAnimationFinished = {
            dispatchAnimationFinished(newHolder)
          }
        )
        true
      } catch (e: Exception) {
        super.animateChange(oldHolder, newHolder, preInfo, postInfo)
      }
    } else {
      return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
    }
  }

  data class NotificationItemInfo(
    var uuid: UUID? = null,
    var isPinned: Boolean = false
  ) : ItemHolderInfo()
}
