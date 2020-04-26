package dev.sasikanth.pinnit.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

// TODO: Complete this for v2
class NotificationsListener : NotificationListenerService() {

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    if (sbn == null || sbn.packageName == packageName) return
  }
}
