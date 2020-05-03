package dev.sasikanth.pinnit.background.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BootCompletedReceiver : BroadcastReceiver() {

  @Inject
  lateinit var repository: NotificationRepository

  @Inject
  lateinit var notificationUtil: NotificationUtil

  @Inject
  lateinit var dispatcherProvider: DispatcherProvider

  private val mainScope by lazy {
    CoroutineScope(SupervisorJob() + dispatcherProvider.main)
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context != null && intent != null && intent.action == Intent.ACTION_BOOT_COMPLETED) {
      context.injector.inject(this)
      val asyncResult = goAsync()

      mainScope.launch {
        runCatching {
          val pinnedNotifications = repository.pinnedNotifications()
          withContext(dispatcherProvider.default) {
            notificationUtil.checkNotificationsVisibility(pinnedNotifications)
          }
        }
        asyncResult.finish()
      }
    }
  }
}
