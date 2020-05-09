package dev.sasikanth.pinnit.qspopup

import dev.sasikanth.pinnit.data.PinnitNotification

sealed class QsPopupEvent

data class NotificationsLoaded(val notifications: List<PinnitNotification>) : QsPopupEvent()

data class NotificationClicked(val notification: PinnitNotification) : QsPopupEvent()

data class TogglePinStatusClicked(val notification: PinnitNotification) : QsPopupEvent()
