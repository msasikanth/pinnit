package dev.sasikanth.pinnit.notifications.adapter

import androidx.recyclerview.widget.DiffUtil
import dev.sasikanth.pinnit.data.PinnitNotification

object NotificationsDiffCallback : DiffUtil.ItemCallback<PinnitNotification>() {
  override fun areItemsTheSame(oldItem: PinnitNotification, newItem: PinnitNotification): Boolean {
    return oldItem.uuid == newItem.uuid
  }

  override fun areContentsTheSame(oldItem: PinnitNotification, newItem: PinnitNotification): Boolean {
    return oldItem == newItem
  }
}
