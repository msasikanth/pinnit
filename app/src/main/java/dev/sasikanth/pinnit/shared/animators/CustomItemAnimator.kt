package dev.sasikanth.pinnit.shared.animators

import android.view.animation.AccelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealCompat
import dev.sasikanth.pinnit.shared.PinnitListAdapter.PinnitItemViewHolder

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
        info.id = pinnitItemViewHolder.pinnitItem?._id ?: 0L
        info.isPinned = pinnitItemViewHolder.isPinned
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

            if (oldPinStatus == newPinStatus || preInfo.id != postInfo.id) {
                super.animateChange(oldHolder, newHolder, preInfo, postInfo)
            }

            try {
                newHolder.pinnedContentView.isVisible = true

                val cx = newHolder.touchCoordinates[0]
                val cy = newHolder.touchCoordinates[1]

                val radius = newHolder.itemView.width.toFloat()
                val anim = if (newPinStatus) {
                    CircularRevealCompat.createCircularReveal(
                        newHolder.pinnedContentView,
                        cx,
                        cy,
                        0.0f,
                        radius
                    )
                } else {
                    CircularRevealCompat.createCircularReveal(
                        newHolder.pinnedContentView,
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
                                unPinnedContentView.isVisible = true
                                pinToggleButton.isChecked = true
                            }
                        },
                        onEnd = {
                            newHolder.apply {
                                unPinnedContentView.isVisible = false
                                pinToggleButton.isVisible = true
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
                                unPinnedContentView.isVisible = true
                            }
                        },
                        onEnd = {
                            newHolder.apply {
                                unPinnedContentView.isVisible = true
                                pinnedContentView.isVisible = false
                                pinToggleButton.isChecked = false
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
