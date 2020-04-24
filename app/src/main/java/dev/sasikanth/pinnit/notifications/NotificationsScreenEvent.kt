package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.data.PinnitNotification

sealed class NotificationsScreenEvent

data class NotificationsLoaded(val notifications: List<PinnitNotification>) : NotificationsScreenEvent()
