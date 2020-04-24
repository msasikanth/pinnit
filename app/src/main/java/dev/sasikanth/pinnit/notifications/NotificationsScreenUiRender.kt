package dev.sasikanth.pinnit.notifications

interface NotificationsScreenUi

class NotificationsScreenUiRender(
  private val ui: NotificationsScreenUi
) {

  fun render(model: NotificationsScreenModel) {
    if (model.notifications == null) {
      return
    }
  }
}
