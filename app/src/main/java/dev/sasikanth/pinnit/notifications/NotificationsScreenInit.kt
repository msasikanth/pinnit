package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

class NotificationsScreenInit : Init<NotificationsScreenModel, NotificationsScreenEffect> {
  override fun init(model: NotificationsScreenModel): First<NotificationsScreenModel, NotificationsScreenEffect> {
    val effects = if (model.notificationsQueried.not()) {
      setOf(LoadNotifications)
    } else {
      emptySet()
    }

    return first(model, effects)
  }
}
