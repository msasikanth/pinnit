package dev.sasikanth.notif.shared

import android.animation.Animator
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealCompat
import dev.sasikanth.notif.R
import dev.sasikanth.notif.shared.NotifListAdapter.NotifItemViewHolder

class CustomItemAnimator : DefaultItemAnimator() {

    private val accelerateInterpolator = AccelerateInterpolator()
    private val fastOutSlowInInterpolator = FastOutSlowInInterpolator()

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    private fun getItemHolderInfo(
        notifItemViewHolder: NotifItemViewHolder,
        info: NotifItemInfo
    ): ItemHolderInfo {
        info.id = notifItemViewHolder.notifItem()?._id ?: 0L
        info.isPinned = notifItemViewHolder.isPinned()
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
        return getItemHolderInfo(viewHolder as NotifItemViewHolder, notifItemInfo)
    }

    override fun recordPostLayoutInformation(
        state: RecyclerView.State,
        viewHolder: RecyclerView.ViewHolder
    ): ItemHolderInfo {
        val notifItemInfo = super.recordPostLayoutInformation(state, viewHolder) as NotifItemInfo
        return getItemHolderInfo(viewHolder as NotifItemViewHolder, notifItemInfo)
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

        return if ((newHolder as? NotifItemViewHolder != null) &&
            (oldHolder as? NotifItemViewHolder != null) &&
            (preInfo as? NotifItemInfo != null) &&
            (postInfo as? NotifItemInfo != null)
        ) {
            val oldPinStatus = preInfo.isPinned
            val newPinStatus = postInfo.isPinned

            if (oldPinStatus == newPinStatus || preInfo.id != postInfo.id) {
                super.animateChange(oldHolder, newHolder, preInfo, postInfo)
            }

            try {
                newHolder.getPinnedContent().isVisible = true

                val cx = newHolder.touchCoordinates[0]
                val cy = newHolder.touchCoordinates[1]

                val radius = newHolder.itemView.width.toFloat()
                val anim: Animator

                if (newPinStatus) {
                    anim = CircularRevealCompat.createCircularReveal(
                        newHolder.getPinnedContent(),
                        cx,
                        cy,
                        0.0f,
                        radius
                    )
                } else {
                    anim = CircularRevealCompat.createCircularReveal(
                        newHolder.getPinnedContent(),
                        cx,
                        cy,
                        radius,
                        0.0f
                    )
                }

                if (newPinStatus) {
                    anim.duration = 250
                    anim.interpolator = fastOutSlowInInterpolator
                    anim.addListener(
                        onStart = {
                            newHolder.apply {
                                getOriginalContent().isVisible = true
                                getNotifPinView().setImageResource(R.drawable.ic_notif_pinned)
                            }
                        },
                        onEnd = {
                            newHolder.apply {
                                getOriginalContent().isVisible = false
                                getPinnedContent().isVisible = true
                                getNotifPinView().setImageResource(R.drawable.ic_notif_pinned)
                                dispatchAnimationFinished(this)
                            }
                        }
                    )
                } else {
                    anim.duration = 200
                    anim.interpolator = accelerateInterpolator
                    anim.addListener(
                        onStart = {
                            newHolder.apply {
                                getOriginalContent().isVisible = true
                                getNotifPinView().setImageResource(R.drawable.ic_notif_pinned)
                            }
                        },
                        onEnd = {
                            newHolder.apply {
                                getOriginalContent().isVisible = true
                                getPinnedContent().isVisible = false
                                getNotifPinView().setImageResource(R.drawable.ic_notif_pin)
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
        var id: Long = 0L
        var isPinned: Boolean = false
    }
}
