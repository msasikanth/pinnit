package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.functions.Consumer
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NotificationsScreenEffectHandler @AssistedInject constructor(
  private val notificationRepository: NotificationRepository,
  private val dispatcherProvider: DispatcherProvider,
  @Assisted private val viewEffectConsumer: Consumer<NotificationScreenViewEffect>
) : CoroutineConnectable<NotificationsScreenEffect, NotificationsScreenEvent>(dispatcherProvider.main) {

  @AssistedInject.Factory
  interface Factory {
    fun create(viewEffectConsumer: Consumer<NotificationScreenViewEffect>): NotificationsScreenEffectHandler
  }

  override suspend fun handler(effect: NotificationsScreenEffect, dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    when (effect) {
      LoadNotifications -> {
        val notificationsFlow = notificationRepository.notifications()
        notificationsFlow
          .onEach { dispatchEvent(NotificationsLoaded(it)) }
          .launchIn(this)
      }
      is OpenNotificationEditor -> {
        viewEffectConsumer.accept(OpenNotificationEditorViewEffect(effect.notificationUuid))
      }
      is ToggleNotificationPinStatus -> {
        notificationRepository.toggleNotificationPinStatus(effect.notification)
      }
      is DeleteNotification -> {
        notificationRepository.deleteNotification(effect.notification)
        viewEffectConsumer.accept(UndoNotificationDeleteViewEffect(effect.notification.uuid))
      }
    }
  }
}
