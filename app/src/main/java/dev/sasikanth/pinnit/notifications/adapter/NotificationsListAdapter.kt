package dev.sasikanth.pinnit.notifications.adapter

import android.text.format.DateUtils
import android.text.format.DateUtils.SECOND_IN_MILLIS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.utils.UtcClock
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.notifications_list_item.*
import org.threeten.bp.Instant

class NotificationsListAdapter(
  utcClock: UtcClock,
  private val onToggleNotificationPinClicked: (PinnitNotification) -> Unit
) : ListAdapter<PinnitNotification, RecyclerView.ViewHolder>(NotificationsDiffCallback) {

  private val now = Instant.now(utcClock)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val context = parent.context
    val view = LayoutInflater.from(context).inflate(R.layout.notifications_list_item, parent, false)
    return NotificationViewHolder(view).apply {
      togglePinIcon.setOnClickListener {
        togglePinIcon.isChecked = !togglePinIcon.isChecked
        onToggleNotificationPinClicked(currentList[adapterPosition])
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is NotificationViewHolder) {
      holder.bind(getItem(position))
    }
  }

  inner class NotificationViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(notification: PinnitNotification) {
      titleTextView.text = notification.title
      contentTextView.text = notification.content

      timeStamp.text = DateUtils.getRelativeTimeSpanString(
        notification.updatedAt.toEpochMilli(),
        now.toEpochMilli(),
        SECOND_IN_MILLIS
      )

      togglePinIcon.isChecked = notification.isPinned
    }
  }
}
