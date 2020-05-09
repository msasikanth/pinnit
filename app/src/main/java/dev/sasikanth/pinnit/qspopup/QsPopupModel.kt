package dev.sasikanth.pinnit.qspopup

import dev.sasikanth.pinnit.data.PinnitNotification

data class QsPopupModel(
  val notifications: List<PinnitNotification>?
) {

  companion object {
    fun default() = QsPopupModel(notifications = null)
  }
}
