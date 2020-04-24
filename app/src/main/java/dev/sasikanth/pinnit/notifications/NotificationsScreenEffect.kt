package dev.sasikanth.pinnit.notifications

import java.util.UUID

sealed class NotificationsScreenEffect

object LoadNotifications : NotificationsScreenEffect()

data class OpenNotificationEditor(val notificationUuid: UUID) : NotificationsScreenEffect()
