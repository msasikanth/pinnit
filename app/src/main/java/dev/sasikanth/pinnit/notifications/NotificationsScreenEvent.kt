package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification
import java.util.UUID

sealed class NotificationsScreenEvent

data class NotificationsLoaded(val notifications: List<PinnitNotification>) : NotificationsScreenEvent()

data class NotificationSwiped(val notification: PinnitNotification) : NotificationsScreenEvent()

data class TogglePinStatusClicked(val notification: PinnitNotification) : NotificationsScreenEvent()

data class UndoNotificationDelete(val notificationUuid: UUID) : NotificationsScreenEvent()

data class NotificationDeleted(val notification: PinnitNotification) : NotificationsScreenEvent()

data class RemovedNotificationSchedule(val notificationId: UUID) : NotificationsScreenEvent()
