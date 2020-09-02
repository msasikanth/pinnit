package dev.sasikanth.pinnit.notifications.adapter

import android.content.res.ColorStateList
import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
import android.text.format.DateUtils.SECOND_IN_MILLIS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealCompat
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.utils.UtcClock
import dev.sasikanth.pinnit.utils.resolveColor
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.notifications_list_item.*
import java.time.Instant
import kotlin.math.hypot

class NotificationsListAdapter(
  utcClock: UtcClock,
  private val onToggleNotificationPinClicked: (PinnitNotification) -> Unit,
  private val onNotificationClicked: (View, PinnitNotification) -> Unit
) : ListAdapter<PinnitNotification, RecyclerView.ViewHolder>(NotificationsDiffCallback) {

  private val now = Instant.now(utcClock)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val context = parent.context
    val view = LayoutInflater.from(context).inflate(R.layout.notifications_list_item, parent, false)
    return NotificationViewHolder(view).apply {
      togglePinIcon.setOnClickListener {
        onToggleNotificationPinClicked(currentList[adapterPosition])
      }

      itemView.setOnClickListener {
        onNotificationClicked(it, currentList[adapterPosition])
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    if (holder is NotificationViewHolder) {
      holder.bind(getItem(position))
    }
  }

  override fun setHasStableIds(hasStableIds: Boolean) {
    super.setHasStableIds(true)
  }

  override fun getItemId(position: Int): Long {
    return currentList[position].uuid.hashCode().toLong()
  }

  inner class NotificationViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    private val context = itemView.context

    val notification: PinnitNotification?
      get() = itemView.tag as? PinnitNotification

    fun bind(notification: PinnitNotification) {
      itemView.tag = notification

      titleTextView.text = notification.title
      contentTextView.text = notification.content
      contentTextView.isVisible = notification.content.isNullOrBlank().not()

      timeStamp.text = DateUtils.getRelativeTimeSpanString(
        notification.updatedAt.toEpochMilli(),
        now.toEpochMilli(),
        SECOND_IN_MILLIS,
        FORMAT_ABBREV_RELATIVE
      )

      togglePinIcon.isChecked = notification.isPinned
      notificationRevealLayout.isVisible = notification.isPinned
      if (notification.isPinned) {
        colorsForNotificationPinned()
      } else {
        colorsForNotificationUnPinned()
      }
      divider.isSelected = notification.isPinned

      itemView.transitionName = "notification_view_${notification.hashCode()}"
    }

    private fun getRevealCx(): Float {
      return (togglePinIcon.left + togglePinIcon.right) / 2f
    }

    private fun getRevealCy(): Float {
      return (togglePinIcon.top + togglePinIcon.bottom) / 2f
    }

    private fun colorsForNotificationUnPinned() {
      appIcon.imageTintList = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorSecondary))
      appName.setTextColor(context.resolveColor(attrRes = R.attr.colorSecondary))
      infoSeparator.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))
      timeStamp.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))

      titleTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackground))
      contentTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))
    }

    private fun colorsForNotificationPinned() {
      appIcon.imageTintList = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      appName.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      infoSeparator.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      timeStamp.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))

      titleTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      contentTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
    }

    fun animateReveal(
      newPinStatus: Boolean,
      fastOutSlowInInterpolator: Interpolator,
      accelerateInterpolator: Interpolator,
      dispatchAnimationFinished: () -> Unit
    ) {
      val cx = getRevealCx()
      val cy = getRevealCy()

      val viewWidth = itemView.width.toFloat()
      val viewHeight = itemView.height.toFloat()
      val viewRadius = hypot(viewWidth, viewHeight)

      val startRadius = if (newPinStatus) 0f else viewRadius
      val endRadius = if (newPinStatus) viewRadius else 0f

      val anim = CircularRevealCompat.createCircularReveal(
        notificationRevealLayout,
        cx,
        cy,
        startRadius,
        endRadius
      )

      if (newPinStatus) {
        anim.duration = 250
        anim.interpolator = fastOutSlowInInterpolator
        anim.addListener(
          onStart = {
            togglePinIcon.isChecked = true
            notificationRevealLayout.isVisible = true
          },
          onEnd = {
            colorsForNotificationPinned()
            dispatchAnimationFinished()
          }
        )
      } else {
        anim.duration = 200
        anim.interpolator = accelerateInterpolator
        anim.addListener(
          onStart = {
            notificationRevealLayout.isVisible = true
          },
          onEnd = {
            notificationRevealLayout.isVisible = false
            togglePinIcon.isChecked = false
            colorsForNotificationUnPinned()
            dispatchAnimationFinished()
          }
        )
      }

      if (anim.isRunning) {
        // Canceling any running animations before starting a new one
        anim.end()
      }

      anim.start()
    }
  }
}
