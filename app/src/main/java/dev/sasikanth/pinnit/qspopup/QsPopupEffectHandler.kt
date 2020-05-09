package dev.sasikanth.pinnit.qspopup

import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class QsPopupEffectHandler @Inject constructor(
  private val dispatcherProvider: DispatcherProvider,
  private val notificationRepository: NotificationRepository
) : CoroutineConnectable<QsPopupEffect, QsPopupEvent>(dispatcherProvider.main) {

  override suspend fun handler(effect: QsPopupEffect, dispatchEvent: (QsPopupEvent) -> Unit) {
    when (effect) {
      LoadNotifications -> loadNotifications(dispatchEvent)
    }
  }

  private fun loadNotifications(dispatchEvent: (QsPopupEvent) -> Unit) {
    notificationRepository
      .notifications()
      .onEach { dispatchEvent(NotificationsLoaded(it)) }
      .launchIn(this)
  }
}
