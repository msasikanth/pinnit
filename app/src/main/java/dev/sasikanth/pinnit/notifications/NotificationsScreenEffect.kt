package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification
import java.util.UUID

sealed class NotificationsScreenEffect

object LoadNotifications : NotificationsScreenEffect()

data class ToggleNotificationPinStatus(val notification: PinnitNotification) : NotificationsScreenEffect()

data class DeleteNotification(val notification: PinnitNotification) : NotificationsScreenEffect()

data class UndoDeletedNotification(val notificationUuid: UUID) : NotificationsScreenEffect()

object CheckNotificationsVisibility : NotificationsScreenEffect()

data class ShowUndoDeleteNotification(val notification: PinnitNotification) : NotificationsScreenEffect()

data class CancelNotificationSchedule(val notificationId: UUID) : NotificationsScreenEffect()

data class RemoveSchedule(val notificationId: UUID) : NotificationsScreenEffect()
