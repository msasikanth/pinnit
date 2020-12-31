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
      is UndoNotificationDelete -> dispatch(setOf(UndoDeletedNotification(event.notificationUuid)))
      is NotificationDeleted -> notificationDeleted(event)
      is RemovedNotificationSchedule -> dispatch(setOf(CancelNotificationSchedule(event.notificationId)))
      is RemoveNotificationScheduleClicked -> dispatch(setOf(RemoveSchedule(event.notificationId)))
    }
  }

  private fun notificationDeleted(event: NotificationDeleted): Next<NotificationsScreenModel, NotificationsScreenEffect> {
    val notification = event.notification
    val effects = mutableSetOf<NotificationsScreenEffect>(ShowUndoDeleteNotification(event.notification))

    if (notification.hasSchedule) {
      effects.add(CancelNotificationSchedule(notification.uuid))
    }

    return dispatch(effects)
  }
}
