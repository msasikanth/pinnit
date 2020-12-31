package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

class QsPopupUpdate : Update<QsPopupModel, QsPopupEvent, QsPopupEffect> {
  override fun update(model: QsPopupModel, event: QsPopupEvent): Next<QsPopupModel, QsPopupEffect> {
    return when (event) {
      is NotificationsLoaded -> next(model.onNotificationsLoaded(notifications = event.notifications))

      is NotificationClicked -> dispatch(setOf(OpenNotificationEditor(event.notification)))

      is TogglePinStatusClicked -> dispatch(setOf(ToggleNotificationPinStatus(event.notification)))

      is RemovedNotificationSchedule -> dispatch(setOf(CancelNotificationSchedule(event.notificationId)))
    }
  }
}
