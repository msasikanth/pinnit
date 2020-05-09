package dev.sasikanth.pinnit.qspopup

import dev.sasikanth.pinnit.data.PinnitNotification

interface QsPopupUi {
  fun showNotifications(notifications: List<PinnitNotification>)
  fun hideNotifications()
  fun showNotificationsEmptyError()
  fun hideNotificationsEmptyError()
}
