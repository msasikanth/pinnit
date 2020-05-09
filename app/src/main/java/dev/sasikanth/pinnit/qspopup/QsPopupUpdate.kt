package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.Next
import com.spotify.mobius.Next.next
import com.spotify.mobius.Update

class QsPopupUpdate : Update<QsPopupModel, QsPopupEvent, QsPopupEffect> {
  override fun update(model: QsPopupModel, event: QsPopupEvent): Next<QsPopupModel, QsPopupEffect> {
    return when (event) {
      is NotificationsLoaded -> next(model.onNotificationsLoaded(notifications = event.notifications))
    }
  }
}
