package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification

data class NotificationsScreenModel(
  val notifications: List<PinnitNotification>?
) {

  companion object {
    fun default() = NotificationsScreenModel(notifications = null)
  }

  val notificationsQueried: Boolean
    get() = !notifications.isNullOrEmpty()

  fun onNotificationsLoaded(notifications: List<PinnitNotification>) = copy(notifications = notifications)
}
