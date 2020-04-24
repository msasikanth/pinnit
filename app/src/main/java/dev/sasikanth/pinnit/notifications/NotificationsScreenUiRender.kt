package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification

interface NotificationsScreenUi {
  fun showNotifications(notifications: List<PinnitNotification>)
  fun showNotificationsEmptyError()
}

class NotificationsScreenUiRender(
  private val ui: NotificationsScreenUi
) {

  fun render(model: NotificationsScreenModel) {
    if (model.notifications == null) {
      return
    }

    if (model.notifications.isNotEmpty()) {
      ui.showNotifications(model.notifications)
    } else {
      ui.showNotificationsEmptyError()
    }
  }
}
