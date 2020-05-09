package dev.sasikanth.pinnit.qspopup

import dev.sasikanth.pinnit.data.PinnitNotification

sealed class QsPopupEffect

object LoadNotifications : QsPopupEffect()

data class OpenNotificationEditor(val notification: PinnitNotification) : QsPopupEffect()
