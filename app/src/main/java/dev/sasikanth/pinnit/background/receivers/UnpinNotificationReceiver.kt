package dev.sasikanth.pinnit.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

  @Inject
  lateinit var notificationUtil: NotificationUtil

  @Inject
  lateinit var dispatcherProvider: DispatcherProvider

  private val job = SupervisorJob()
  private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.d(TAG, "Failed to unpin the notification.", throwable)
  }
  private val mainScope by lazy {
    CoroutineScope(job + dispatcherProvider.main + coroutineExceptionHandler)
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    val notificationUuid = intent?.getStringExtra(EXTRA_NOTIFICATION_UUID)

    if (context != null && intent != null && intent.action == ACTION_UNPIN && notificationUuid != null) {
      context.injector.inject(this)
      val asyncResult = goAsync()

      mainScope.launch {
        val notification = repository.notification(UUID.fromString(notificationUuid))
        repository.updatePinStatus(notification.uuid, false)

        notificationUtil.dismissNotification(notification)

        asyncResult.finish()
      }
    }
  }
}
