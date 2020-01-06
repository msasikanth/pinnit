package dev.sasikanth.pinnit.shared

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.TemplateStyle
import dev.sasikanth.pinnit.utils.DateUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.pinnit_item.*

class PinnitListAdapter(
    private val pinnitAdapterListener: PinnitAdapterListener
) : ListAdapter<PinnitItem, RecyclerView.ViewHolder>(PinnitDiffCallback) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return PinnitItemViewHolder.from(parent).apply {
      toggleNotificationPin.setOnClickListener {
        pinnitAdapterListener.pinNotifItem(pinnitItem)
      }
      itemView.setOnClickListener {
        pinnitAdapterListener.onPinnitItemClick(pinnitItem)
      }
      itemView.setOnLongClickListener {
        pinnitAdapterListener.pinNotifItem(pinnitItem)
        return@setOnLongClickListener true
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is PinnitItemViewHolder) {
      holder.pinnitItem = getItem(position)
      holder.render()
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  class PinnitItemViewHolder private constructor(
      override val containerView: View
  ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    companion object {
      fun from(parent: ViewGroup): PinnitItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.pinnit_item, parent, false)
        return PinnitItemViewHolder(itemView)
      }
    }

    private val context: Context = itemView.context
    private val colorDrawable = ColorDrawable(context.resolveColor(attrRes = R.attr.colorPrimary))

    lateinit var pinnitItem: PinnitItem

    fun render() {
      pinnedRevealLayout.isVisible = pinnitItem.isPinned
      toggleNotificationPin.isChecked = pinnitItem.isPinned

      appName.text = pinnitItem.appLabel
      timestamp.text = DateUtils.getRelativeTime(pinnitItem.postedOn)
      pinnitIconView.load(pinnitItem.notifIcon) {
        placeholder(colorDrawable)
        error(colorDrawable)
      }
      pinnitTitleView.text = pinnitItem.title

      val contentViewMessage = if (pinnitItem.template == TemplateStyle.MessagingStyle) {
        val messagesBuilder = StringBuilder()
        val lastMessages = pinnitItem.messages.takeLast(5).sortedBy { it.timestamp }

        lastMessages.forEachIndexed { index, message ->
          val messageText = "${message.senderName}: ${message.message}"
          if (index == lastMessages.lastIndex) {
            messagesBuilder.append(messageText)
          } else {
            messagesBuilder.appendln(messageText)
          }
        }
        messagesBuilder.toString()
      } else {
        pinnitItem.content
      }
      pinnitContentView.text = contentViewMessage

      updateColorsBasedOnPinStatus()
    }

    fun updateColorsBasedOnPinStatus() {
      if (pinnitItem.isPinned) {
        notificationPinnedState()
      } else {
        notificationUnPinnedState()
      }
    }

    fun getRevealCx(): Float {
      return (toggleNotificationPin.left + toggleNotificationPin.right) / 2f
    }

    fun getRevealCy(): Float {
      return (toggleNotificationPin.top + toggleNotificationPin.bottom) / 2f
    }

    private fun notificationUnPinnedState() {
      appSmallIcon.imageTintList = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorSecondary))
      editButton.setTextColor(context.resolveColor(attrRes = R.attr.colorSecondary))
      pinnitTitleView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackground))
      pinnitContentView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))
      appName.setTextColor(context.resolveColor(attrRes = R.attr.colorSecondary))
      separator.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))
      timestamp.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))
    }

    private fun notificationPinnedState() {
      appSmallIcon.imageTintList = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      editButton.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      pinnitTitleView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      pinnitContentView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      appName.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      separator.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      timestamp.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
    }
  }
}

object PinnitDiffCallback : DiffUtil.ItemCallback<PinnitItem>() {
  override fun areItemsTheSame(oldItem: PinnitItem, newItem: PinnitItem): Boolean {
    return oldItem.notifKey == newItem.notifKey
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
