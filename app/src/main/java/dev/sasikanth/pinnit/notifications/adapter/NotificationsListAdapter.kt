package dev.sasikanth.pinnit.notifications.adapter

import android.content.res.ColorStateList
import android.text.format.DateUtils
import android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE
import android.text.format.DateUtils.SECOND_IN_MILLIS
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import androidx.appcompat.widget.PopupMenu
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealCompat
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import dev.sasikanth.pinnit.utils.resolveColor
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.notifications_list_item.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.hypot

class NotificationsListAdapter(
  utcClock: UtcClock,
  private val userClock: UserClock,
  private val scheduleDateFormatter: DateTimeFormatter,
  private val scheduleTimeFormatter: DateTimeFormatter,
  private val onToggleNotificationPinClicked: (PinnitNotification) -> Unit,
  private val onNotificationClicked: (View, PinnitNotification) -> Unit,
  private val onEditNotificationScheduleClicked: (PinnitNotification) -> Unit,
  private val onRemoveNotificationScheduleClicked: (PinnitNotification) -> Unit
) : ListAdapter<PinnitNotification, RecyclerView.ViewHolder>(NotificationsDiffCallback) {

  private val now = Instant.now(utcClock)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val context = parent.context
    val view = LayoutInflater.from(context).inflate(R.layout.notifications_list_item, parent, false)
    return NotificationViewHolder(view).apply {
      togglePinIcon.setOnClickListener {
        onToggleNotificationPinClicked(currentList[adapterPosition])
      }

      scheduleButton.setOnClickListener {
        val popupMenu = PopupMenu(context, it, Gravity.START)
        popupMenu.inflate(R.menu.notification_schedule)
        popupMenu.setOnMenuItemClickListener { item ->
          when (item.itemId) {
            R.id.editSchedule -> {
              val notification = currentList[adapterPosition]
              onEditNotificationScheduleClicked(notification)
            }
            R.id.removeSchedule -> {
              val notification = currentList[adapterPosition]
              onRemoveNotificationScheduleClicked(notification)
            }
          }
          return@setOnMenuItemClickListener true
        }
        popupMenu.show()
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

      renderScheduleButton(notification)

      itemView.transitionName = "notification_view_${notification.hashCode()}"
    }

    private fun renderScheduleButton(notification: PinnitNotification) {
      val userCurrentDateTime = LocalDateTime.now(userClock)
      val schedule = notification.schedule

      scheduleButton.isVisible = schedule != null
      if (schedule == null) return

      val scheduleDateTime = schedule.scheduleDate!!.atTime(schedule.scheduleTime!!)

      val isInFuture = scheduleDateTime.isAfter(userCurrentDateTime)
      val isPinned = notification.isPinned
      val scheduleIsRepeatable = schedule.scheduleType != null

      scheduleButton.isEnabled = isInFuture
      if (isPinned && !scheduleIsRepeatable) {
        scheduleButton.isVisible = false
      }

      scheduleButton.text = scheduleButtonText(scheduleDateTime, userCurrentDateTime)
    }

    private fun scheduleButtonText(
      scheduleDateTime: LocalDateTime,
      userCurrentDateTime: LocalDateTime
    ): String {
      val yesterday = LocalDateTime.now(userClock).minusDays(1)
      val tomorrow = LocalDateTime.now(userClock).plusDays(1)

      return when {
        scheduleDateTime.toLocalDate().isEqual(userCurrentDateTime.toLocalDate()) -> {
          context.getString(
            R.string.notification_schedule_button_today,
            scheduleTimeFormatter.format(scheduleDateTime.toLocalTime())
          )
        }
        scheduleDateTime.toLocalDate().isEqual(yesterday.toLocalDate()) -> {
          context.getString(
            R.string.notification_schedule_button_yesterday,
            scheduleTimeFormatter.format(scheduleDateTime.toLocalTime())
          )
        }
        scheduleDateTime.toLocalDate().isEqual(tomorrow.toLocalDate()) -> {
          context.getString(
            R.string.notification_schedule_button_tomorrow,
            scheduleTimeFormatter.format(scheduleDateTime.toLocalTime())
          )
        }
        else -> {
          context.getString(
            R.string.notification_schedule_button,
            scheduleDateFormatter.format(scheduleDateTime.toLocalDate()),
            scheduleTimeFormatter.format(scheduleDateTime.toLocalTime())
          )
        }
      }
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

      scheduleButton.strokeColor = ContextCompat.getColorStateList(context, R.color.schedule_indicator_stroke_state)
      scheduleButton.setTextColor(ContextCompat.getColorStateList(context, R.color.schedule_indicator_text_state))
      scheduleButton.iconTint = ContextCompat.getColorStateList(context, R.color.schedule_indicator_icon_state)
    }

    private fun colorsForNotificationPinned() {
      appIcon.imageTintList = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      appName.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      infoSeparator.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      timeStamp.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))

      titleTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      contentTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))

      scheduleButton.strokeColor = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      scheduleButton.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      scheduleButton.iconTint = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorOnPrimary))
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
