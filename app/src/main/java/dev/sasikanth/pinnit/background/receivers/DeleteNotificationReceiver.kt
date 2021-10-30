package dev.sasikanth.pinnit.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class DeleteNotificationReceiver : BroadcastReceiver() {

  companion object {
    private const val TAG = "DeleteNotificationReceiver"

    const val ACTION_DELETE = "dev.sasikanth.pinnit.system.action.DeleteNotification"
    const val EXTRA_NOTIFICATION_UUID = "dev.sasikanth.system.DeleteNotificationReceiver:notificationUuid"
  }

  @Inject
  lateinit var repository: NotificationRepository

  @Inject
  lateinit var notificationUtil: NotificationUtil

  @Inject
  lateinit var dispatcherProvider: DispatcherProvider

  private val job = SupervisorJob()
  private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.d(TAG, "Failed to delete the notification.", throwable)
  }
  private val mainScope by lazy {
    CoroutineScope(job + dispatcherProvider.main + coroutineExceptionHandler)
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    val notificationUuid = intent?.getStringExtra(EXTRA_NOTIFICATION_UUID)

    if (context != null && intent != null && intent.action == ACTION_DELETE && notificationUuid != null) {
      val asyncResult = goAsync()

      mainScope.launch {
        val notificationId = UUID.fromString(notificationUuid)
        val notification = repository.notification(notificationId)
        repository.updatePinStatus(notification.uuid, false)
        repository.deleteNotification(notification)

        notificationUtil.dismissNotification(notification)

        asyncResult.finish()
      }
    }
  }
}
