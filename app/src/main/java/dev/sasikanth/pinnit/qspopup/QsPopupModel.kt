package dev.sasikanth.pinnit.qspopup

import android.os.Parcelable
import dev.sasikanth.pinnit.data.PinnitNotification
import kotlinx.parcelize.Parcelize

@Parcelize
data class QsPopupModel(
  val notifications: List<PinnitNotification>?
) : Parcelable {

  companion object {
    fun default() = QsPopupModel(notifications = null)
  }

  val notificationsQueried: Boolean
    get() = !notifications.isNullOrEmpty()

  fun onNotificationsLoaded(notifications: List<PinnitNotification>) = copy(notifications = notifications)
}
