package dev.sasikanth.pinnit.shared

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealFrameLayout
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.databinding.PinnitItemBinding
import dev.sasikanth.pinnit.utils.CheckableImageView

class PinnitListAdapter(private val pinnitAdapterListener: PinnitAdapterListener) :
    ListAdapter<PinnitItem, RecyclerView.ViewHolder>(PinnitDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PinnitItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PinnitItemViewHolder) {
            holder.bind(
                pinnitItem = getItem(position),
                pinnitAdapterListener = pinnitAdapterListener
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    class PinnitItemViewHolder private constructor(private val binding: PinnitItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): PinnitItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PinnitItemBinding.inflate(layoutInflater, parent, false)
                return PinnitItemViewHolder(binding)
            }
        }

        val touchCoordinates = floatArrayOf(0f, 0f)

        init {
            itemView.setOnTouchListener { _, motionEvent ->
                if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchCoordinates[0] = motionEvent.x
                    touchCoordinates[1] = motionEvent.y
                }
                return@setOnTouchListener false
            }

            binding.notifPin.setOnTouchListener { _, motionEvent ->
                if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchCoordinates[0] = (itemView.width - 28.px).toFloat()
                    touchCoordinates[1] = 28.px.toFloat()
                }
                return@setOnTouchListener false
            }
        }

        fun bind(
            pinnitItem: PinnitItem,
            pinnitAdapterListener: PinnitAdapterListener
        ) {
            binding.pinnitItem = pinnitItem
            binding.pinnitAdapterListener = pinnitAdapterListener
            binding.notifPinnedContent.tag = pinnitItem.isPinned
            binding.executePendingBindings()

            binding.notifPinnedContent.isVisible = pinnitItem.isPinned
            binding.notifOriginalContent.isVisible = !pinnitItem.isPinned
            binding.notifPin.isChecked = pinnitItem.isPinned
        }

        fun pinnitItem(): PinnitItem? {
            return binding.pinnitItem
        }

        fun isPinned(): Boolean {
            return binding.pinnitItem?.isPinned ?: false
        }

        fun getPinnedContent(): CircularRevealFrameLayout {
            return binding.notifPinnedContent
        }

        fun getOriginalContent(): ConstraintLayout {
            return binding.notifOriginalContent
        }

        fun getPinButton(): CheckableImageView {
            return binding.notifPin
        }
    }
}

object PinnitDiffCallback : DiffUtil.ItemCallback<PinnitItem>() {
    override fun areItemsTheSame(oldItem: PinnitItem, newItem: PinnitItem): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: PinnitItem, newItem: PinnitItem): Boolean {
        return oldItem == newItem
    }
}

class PinnitAdapterListener(
    private val pinNotification: (pinnitItem: PinnitItem, isPinned: Boolean) -> Unit,
    private val onNotificationClicked: (pinnitItem: PinnitItem) -> Unit
) {
    fun onPinnitItemClick(pinnitItem: PinnitItem) {
        onNotificationClicked(pinnitItem)
    }

    fun pinNotifItem(pinnitItem: PinnitItem): Boolean {
        pinNotification(pinnitItem, !pinnitItem.isPinned)
        return true
    }
}
