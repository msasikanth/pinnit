package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification
import java.util.UUID

sealed class NotificationScreenViewEffect

data class OpenNotificationEditorViewEffect(val notification: PinnitNotification) : NotificationScreenViewEffect()

data class UndoNotificationDeleteViewEffect(val notificationUuid: UUID) : NotificationScreenViewEffect()
