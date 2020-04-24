package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.First
import com.spotify.mobius.First.first
import com.spotify.mobius.Init

class NotificationsScreenInit : Init<NotificationsScreenModel, NotificationsScreenEffect> {
  override fun init(model: NotificationsScreenModel): First<NotificationsScreenModel, NotificationsScreenEffect> {
    return first(model, setOf(LoadNotifications))
  }
}
