package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

class NotificationsScreenInit : Init<NotificationsScreenModel, NotificationsScreenEffect> {
  override fun init(model: NotificationsScreenModel): First<NotificationsScreenModel, NotificationsScreenEffect> {
    val effects = if (model.notificationsQueried.not()) {
      // We are only checking for notifications visibility during
      // screen create because system notifications are disappear only
      // if the app is force closed (or when updating). So app needs
      // to be reopened again. Notifications will persist
      // orientation changes, so no point checking again.
      setOf(LoadNotifications, CheckNotificationsVisibility)
    } else {
      emptySet()
    }

    return first(model, effects)
  }
}
