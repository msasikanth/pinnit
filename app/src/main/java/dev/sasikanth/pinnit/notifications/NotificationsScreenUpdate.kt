package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

class NotificationsScreenUpdate : Update<NotificationsScreenModel, NotificationsScreenEvent, NotificationsScreenEffect> {
  override fun update(model: NotificationsScreenModel, event: NotificationsScreenEvent): Next<NotificationsScreenModel, NotificationsScreenEffect> {
    return when (event) {
      is NotificationsLoaded -> next(model.onNotificationsLoaded(event.notifications))
      is NotificationSwiped -> dispatch(setOf(DeleteNotification(event.notification)))
      is TogglePinStatusClicked -> dispatch(setOf(ToggleNotificationPinStatus(event.notification)))
      is NotificationClicked -> dispatch(setOf(OpenNotificationEditor(event.notificationUuid)))
    }
  }
}
