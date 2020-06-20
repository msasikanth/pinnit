package dev.sasikanth.pinnit.utils.notification

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.navigation.NavDeepLinkBuilder
import dev.sasikanth.pinnit.R
import dev.sasikanth.pinnit.activity.MainActivity
import dev.sasikanth.pinnit.background.receivers.UnpinNotificationReceiver
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.di.AppScope
import dev.sasikanth.pinnit.editor.EditorScreenArgs
import javax.inject.Inject

@AppScope
class NotificationUtil @Inject constructor(
  private val context: Application
) {

  companion object {
    private const val CHANNEL_ID = "dev.sasikanth.pinnit.utils.notification:NotificationUtil:pinned_notifications"
  }

  private val notificationManager = NotificationManagerCompat.from(context)
  private val systemNotificationManager = context.getSystemService<NotificationManager>()

  fun showNotification(pinnitNotification: PinnitNotification) {
    createNotificationChannel()
    val notification =
      buildSystemNotification(pinnitNotification)

    with(notificationManager) {
      notify(pinnitNotification.uuid.hashCode(), notification)
    }
  }

  fun dismissNotification(pinnitNotification: PinnitNotification) {
    notificationManager.cancel(pinnitNotification.uuid.hashCode())
  }

  /**
   * This will check for system notification visibility of pinned notifications. If the
   * notification is not visible for a pinned pinnit notification. It will show the system
   * notification for it.
   *
   * We are doing this because of certain system actions that will remove the notification
   * from system tray.
   *
   * - When app is updated: This will stop the app causing the system notifications
   * to be dismissed.
   * - When app is force stopped: This will cause the system notifications to be dismissed
   *
   * When those scenarios happen, we want the app to re-show the system notifications when
   * app is opened again.
   */
  fun checkNotificationsVisibility(pinnedNotifications: List<PinnitNotification>) {
    if (systemNotificationManager != null) {
      val activeNotifications = systemNotificationManager.activeNotifications
      for (notification in pinnedNotifications) {
        val id = notification.uuid.hashCode()
        val isNotificationVisible = activeNotifications.any { it.id == id }
        if (isNotificationVisible.not()) {
          showNotification(notification)
        }
      }
    }
  }

  private fun buildSystemNotification(notification: PinnitNotification): Notification {
    val content = notification.content.orEmpty()

    val editorPendingIntent = NavDeepLinkBuilder(context)
      .setGraph(R.navigation.main_nav_graph)
      .setComponentName(MainActivity::class.java)
      .setDestination(R.id.editorScreen)
      .setArguments(EditorScreenArgs(notification.uuid.toString()).toBundle())
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

  private fun createNotificationChannel() {
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
