package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification

interface NotificationsScreenUi {
  fun showNotifications(notifications: List<PinnitNotification>)
  fun showNotificationsEmptyError()
  fun hideNotificationsEmptyError()
  fun hideNotifications()
}

class NotificationsScreenUiRender(
  private val ui: NotificationsScreenUi
) {

  fun render(model: NotificationsScreenModel) {
    if (model.notifications == null) {
      return
    }

    if (model.notifications.isNotEmpty()) {
      ui.hideNotificationsEmptyError()
      ui.showNotifications(model.notifications)
    } else {
      ui.hideNotifications()
      ui.showNotificationsEmptyError()
    }
  }
}
