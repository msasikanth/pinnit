package dev.sasikanth.pinnit.notifications

import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NotificationsScreenEffectHandler @Inject constructor(
  private val notificationRepository: NotificationRepository,
  dispatcherProvider: DispatcherProvider
) : CoroutineConnectable<NotificationsScreenEffect, NotificationsScreenEvent>(dispatcherProvider.main) {

  override suspend fun handler(effect: NotificationsScreenEffect, dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    when (effect) {
      LoadNotifications -> {
        val notificationsFlow = notificationRepository.notifications()
        notificationsFlow
          .onEach { dispatchEvent(NotificationsLoaded(it)) }
          .launchIn(this)
      }
    }
  }
}
