package dev.sasikanth.pinnit.qspopup

class QsPopupUiRenderer(
  private val ui: QsPopupUi
) {

  fun render(model: QsPopupModel) {
    if (model.notifications == null) {
      return
    }

    if (model.notifications.isNotEmpty()) {
      ui.showNotifications(model.notifications)
      ui.hideNotificationsEmptyError()
    } else {
      ui.hideNotifications()
      ui.showNotificationsEmptyError()
    }
  }
}
