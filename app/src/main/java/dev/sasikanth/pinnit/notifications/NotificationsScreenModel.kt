package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification

data class NotificationsScreenModel(
  val notifications: List<PinnitNotification>?
) {

  companion object {
    fun default() = NotificationsScreenModel(notifications = null)
  }

  fun onNotificationsLoaded(notifications: List<PinnitNotification>) = copy(notifications = notifications)
}
