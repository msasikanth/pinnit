package dev.sasikanth.pinnit.shared

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.TemplateStyle

val Int.px: Int
  get() = (Resources.getSystem().displayMetrics.density * this).toInt()

val Int.dp: Int
  get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Context.isNightMode: Boolean
  get() {
    return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
      Configuration.UI_MODE_NIGHT_YES -> true
      else -> false
    }
  }

fun Context.dismissNotification(pinnitItem: PinnitItem) {
  val notificationId = pinnitItem.contentHashCode()
  NotificationManagerCompat.from(this).cancel(notificationId)
}

fun Context.showPersistentNotif(pinnitItem: PinnitItem) {
  // Create the NotificationChannel, but only on API 26+ because
  // the NotificationChannel class is new and not in the support library
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val name = getString(R.string.channel_pinned)
    val importance = NotificationManager.IMPORTANCE_MIN
    val channel = NotificationChannel(getString(R.string.channel_pinned), name, importance)
    // Register the channel with the system
    NotificationManagerCompat.from(this).createNotificationChannel(channel)
  }

  val notificationBuilder = if (
      pinnitItem.template == TemplateStyle.DefaultStyle ||
      pinnitItem.template == TemplateStyle.BigTextStyle ||
      pinnitItem.template == TemplateStyle.InboxStyle
  ) {
    NotificationCompat.Builder(this, getString(R.string.channel_pinned))
        .setSmallIcon(R.drawable.ic_pinnit_notification)
        .setContentTitle(pinnitItem.title)
        .setContentText(pinnitItem.content)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(pinnitItem.content)
        )
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
  } else if (pinnitItem.template == TemplateStyle.MessagingStyle) {
    val messageStyle = NotificationCompat.MessagingStyle(
        Person.Builder()
            .setName("Me")
            .build()
    ).setConversationTitle(pinnitItem.title)

    pinnitItem.messages.forEach {
      messageStyle.addMessage(
          it.message, it.timestamp, Person.Builder()
          .setName(it.senderName)
          .build()
      )
    }

    NotificationCompat.Builder(this, getString(R.string.channel_pinned))
        .setSmallIcon(R.drawable.ic_pinnit_notification)
        .setStyle(messageStyle)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
  } else {
    NotificationCompat.Builder(this, getString(R.string.channel_pinned))
        .setSmallIcon(R.drawable.ic_pinnit_notification)
        .setContentTitle(pinnitItem.title)
        .setContentText(pinnitItem.content)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
  }

  with(NotificationManagerCompat.from(this)) {
    val notificationId = pinnitItem.contentHashCode()
    notify(notificationId, notificationBuilder.build())
  }
}

@ColorInt
fun Context.resolveColor(
    @ColorRes colorRes: Int? = null,
    @AttrRes attrRes: Int? = null,
    fallback: (() -> Int)? = null
): Int {
  if (attrRes != null) {
    val a = theme.obtainStyledAttributes(intArrayOf(attrRes))
    try {
      val result = a.getColor(0, 0)
      if (result == 0 && fallback != null) {
        return fallback()
      }
      return result
    } finally {
      a.recycle()
    }
  }
  return ContextCompat.getColor(this, colorRes ?: 0)
}

inline fun Drawable.asBitmap(): Bitmap {
  val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)

  setBounds(0, 0, canvas.width, canvas.height)
  draw(canvas)

  return bitmap
}

inline fun View.fullScreen() {
  systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
      View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
      View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}
