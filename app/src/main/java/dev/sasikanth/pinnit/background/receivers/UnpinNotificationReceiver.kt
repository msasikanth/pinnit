package dev.sasikanth.pinnit.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.system.NotificationUtil
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

class UnpinNotificationReceiver : BroadcastReceiver() {

  companion object {
    private const val TAG = "UnpinNotification"

    const val ACTION_UNPIN = "dev.sasikanth.pinnit.system.action.NotificationUnpin"
    const val EXTRA_NOTIFICATION_UUID = "dev.sasikanth.system.UnpinNotificationReceiver:notificationUuid"
  }

  @Inject
  lateinit var repository: NotificationRepository

  override fun onReceive(context: Context?, intent: Intent?) {
    val asyncResult = goAsync()
    val notificationUuid = intent?.getStringExtra(EXTRA_NOTIFICATION_UUID)

    if (context != null && intent != null && intent.action == ACTION_UNPIN && notificationUuid != null) {
      context.injector.inject(this)

      try {
        val notification = runBlocking {
          val notification = repository.notification(UUID.fromString(notificationUuid))
          repository.toggleNotificationPinStatus(notification)
          notification
        }
        NotificationUtil.dismissNotification(context, notification)
      } catch (e: RuntimeException) {
        Log.e(TAG, "Cannot find the notification, it might already be unpinned.")
      }
    }
    asyncResult.finish()
  }
}
