package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification

sealed class NotificationsScreenEvent

data class NotificationsLoaded(val notifications: List<PinnitNotification>) : NotificationsScreenEvent()

data class NotificationSwiped(val notification: PinnitNotification) : NotificationsScreenEvent()

data class TogglePinStatusClicked(val notification: PinnitNotification) : NotificationsScreenEvent()
