package dev.sasikanth.pinnit.notifications

import android.os.Parcelable
import dev.sasikanth.pinnit.data.PinnitNotification
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationsScreenModel(
  val notifications: List<PinnitNotification>?
) : Parcelable {

  companion object {
    fun default() = NotificationsScreenModel(notifications = null)
  }

  val notificationsQueried: Boolean
    get() = !notifications.isNullOrEmpty()

  fun onNotificationsLoaded(notifications: List<PinnitNotification>) = copy(notifications = notifications)
}
