package dev.sasikanth.pinnit.qspopup

import dev.sasikanth.pinnit.data.PinnitNotification

sealed class QsPopupEvent

data class NotificationsLoaded(val notifications: List<PinnitNotification>) : QsPopupEvent()
