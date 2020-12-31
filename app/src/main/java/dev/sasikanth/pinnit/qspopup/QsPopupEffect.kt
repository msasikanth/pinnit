package dev.sasikanth.pinnit.qspopup

import dev.sasikanth.pinnit.data.PinnitNotification
import java.util.UUID

sealed class QsPopupEffect

object LoadNotifications : QsPopupEffect()

data class OpenNotificationEditor(val notification: PinnitNotification) : QsPopupEffect()

data class ToggleNotificationPinStatus(val notification: PinnitNotification) : QsPopupEffect()

data class CancelNotificationSchedule(val notificationId: UUID) : QsPopupEffect()
