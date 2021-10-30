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
import dev.sasikanth.pinnit.databinding.NotificationsListItemBinding
import dev.sasikanth.pinnit.utils.UserClock
import dev.sasikanth.pinnit.utils.UtcClock
import dev.sasikanth.pinnit.utils.resolveColor
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
    val layoutInflater = LayoutInflater.from(context)
    val binding = NotificationsListItemBinding.inflate(layoutInflater, parent, false)

    return NotificationViewHolder(binding).apply {
      binding.togglePinIcon.setOnClickListener {
        onToggleNotificationPinClicked(currentList[adapterPosition])
      }

      binding.scheduleButton.setOnClickListener {
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

  inner class NotificationViewHolder(
    private val binding: NotificationsListItemBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    private val context = itemView.context

    val notification: PinnitNotification?
      get() = itemView.tag as? PinnitNotification

    fun bind(notification: PinnitNotification) {
      itemView.tag = notification

      binding.titleTextView.text = notification.title
      binding.contentTextView.text = notification.content
      binding.contentTextView.isVisible = notification.content.isNullOrBlank().not()

      binding.timeStamp.text = DateUtils.getRelativeTimeSpanString(
        notification.updatedAt.toEpochMilli(),
        now.toEpochMilli(),
        SECOND_IN_MILLIS,
        FORMAT_ABBREV_RELATIVE
      )

      binding.togglePinIcon.isChecked = notification.isPinned
      binding.notificationRevealLayout.isVisible = notification.isPinned
      if (notification.isPinned) {
        colorsForNotificationPinned()
      } else {
        colorsForNotificationUnPinned()
      }
      binding.divider.isSelected = notification.isPinned

      renderScheduleButton(notification)

      itemView.transitionName = "notification_view_${notification.hashCode()}"
    }

    private fun renderScheduleButton(notification: PinnitNotification) {
      val userCurrentDateTime = LocalDateTime.now(userClock)
      val schedule = notification.schedule

      binding.scheduleButton.isVisible = schedule != null
      if (schedule == null) return

      val scheduleDateTime = schedule.scheduleDate!!.atTime(schedule.scheduleTime!!)

      val isInFuture = scheduleDateTime.isAfter(userCurrentDateTime)
      val isPinned = notification.isPinned
      val scheduleIsRepeatable = schedule.scheduleType != null

      // Had to use `state_selected` for CSL instaed of `state_enabled`
      // because of weird issues when pinning and un-pinning. Need to resolve
      // it later.

      // Reproducing steps: Pin a past note with schedule, close the app, open the app, unpin note
      // with `state_enabled` in CSL the icon color is not set to disabled color.
      binding.scheduleButton.isSelected = isInFuture
      binding.scheduleButton.isEnabled = isInFuture

      if (isPinned && !scheduleIsRepeatable) {
        binding.scheduleButton.isVisible = false
      }

      binding.scheduleButton.text = scheduleButtonText(scheduleDateTime, userCurrentDateTime)
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
      return (binding.togglePinIcon.left + binding.togglePinIcon.right) / 2f
    }

    private fun getRevealCy(): Float {
      return (binding.togglePinIcon.top + binding.togglePinIcon.bottom) / 2f
    }

    private fun colorsForNotificationUnPinned() {
      binding.appIcon.imageTintList = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorSecondary))
      binding.appName.setTextColor(context.resolveColor(attrRes = R.attr.colorSecondary))
      binding.infoSeparator.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))
      binding.timeStamp.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))

      binding.titleTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackground))
      binding.contentTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnBackgroundVariant))

      binding.scheduleButton.strokeColor = ContextCompat.getColorStateList(context, R.color.schedule_indicator_stroke_state)
      binding.scheduleButton.setTextColor(ContextCompat.getColorStateList(context, R.color.schedule_indicator_text_state))
      binding.scheduleButton.iconTint = ContextCompat.getColorStateList(context, R.color.schedule_indicator_icon_state)
    }

    private fun colorsForNotificationPinned() {
      binding.appIcon.imageTintList = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      binding.appName.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      binding.infoSeparator.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))
      binding.timeStamp.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))

      binding.titleTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      binding.contentTextView.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimaryVariant))

      binding.scheduleButton.strokeColor = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      binding.scheduleButton.setTextColor(context.resolveColor(attrRes = R.attr.colorOnPrimary))
      binding.scheduleButton.iconTint = ColorStateList.valueOf(context.resolveColor(attrRes = R.attr.colorOnPrimary))
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
        binding.notificationRevealLayout,
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
            binding.togglePinIcon.isChecked = true
            binding.notificationRevealLayout.isVisible = true
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
            binding.notificationRevealLayout.isVisible = true
          },
          onEnd = {
            binding.notificationRevealLayout.isVisible = false
            binding.togglePinIcon.isChecked = false
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
