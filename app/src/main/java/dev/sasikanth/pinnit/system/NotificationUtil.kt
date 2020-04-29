package dev.sasikanth.pinnit.system

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.background.receivers.UnpinNotificationReceiver
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.editor.EditorScreenArgs

object NotificationUtil {

  private const val CHANNEL_ID = "pinned_notifications"

  fun showNotification(context: Context, pinnitNotification: PinnitNotification) {
    createNotificationChannel(context)
    val notification = buildSystemNotification(context, pinnitNotification)

    with(NotificationManagerCompat.from(context)) {
      notify(pinnitNotification.uuid.hashCode(), notification)
    }
  }

  fun dismissNotification(context: Context, pinnitNotification: PinnitNotification) {
    NotificationManagerCompat.from(context).cancel(pinnitNotification.uuid.hashCode())
  }

  private fun buildSystemNotification(context: Context, notification: PinnitNotification): Notification {
    val content = notification.content.orEmpty()

    val editorPendingIntent = NavDeepLinkBuilder(context)
      .setGraph(R.navigation.main_nav_graph)
      .setDestination(R.id.editorScreen)
      .setArguments(EditorScreenArgs(notification).toBundle())
      .createPendingIntent()

    val unpinIntent = Intent(context, UnpinNotificationReceiver::class.java).apply {
      action = UnpinNotificationReceiver.ACTION_UNPIN
      putExtra(UnpinNotificationReceiver.EXTRA_NOTIFICATION_UUID, notification.uuid.toString())
    }
    val unpinPendingIntent = PendingIntent.getBroadcast(
      context,
      notification.uuid.hashCode(),
      unpinIntent,
      PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_pinnit_notification)
      .setContentTitle(notification.title)
      .setContentText(content)
      .setContentIntent(editorPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .setStyle(
        NotificationCompat.BigTextStyle().bigText(
          notification.content
        )
      )
      .addAction(R.drawable.ic_pinnit_pin, context.getString(R.string.unpin), unpinPendingIntent)
      .setOngoing(true)

    return builder.build()
  }

  private fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = context.getString(R.string.channel_name)
      val importance = NotificationManager.IMPORTANCE_LOW
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        setShowBadge(false)
        enableLights(false)
      }
      // Register the channel with the system
      val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }
}
