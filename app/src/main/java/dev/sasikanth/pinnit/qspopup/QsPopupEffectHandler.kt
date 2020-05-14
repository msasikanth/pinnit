package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.functions.Consumer
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class QsPopupEffectHandler @AssistedInject constructor(
  private val dispatcherProvider: DispatcherProvider,
  private val notificationRepository: NotificationRepository,
  private val notificationUtil: NotificationUtil,
  @Assisted private val viewEffectConsumer: Consumer<QsPopupViewEffect>
) : CoroutineConnectable<QsPopupEffect, QsPopupEvent>(dispatcherProvider.main) {

  @AssistedInject.Factory
  interface Factory {
    fun create(viewEffectConsumer: Consumer<QsPopupViewEffect>): QsPopupEffectHandler
  }

  override suspend fun handler(effect: QsPopupEffect, dispatchEvent: (QsPopupEvent) -> Unit) {
    when (effect) {
      LoadNotifications -> loadNotifications(dispatchEvent)
      is OpenNotificationEditor -> viewEffectConsumer.accept(OpenNotificationEditorViewEffect(effect.notification))

      is ToggleNotificationPinStatus -> toggleNotificationPinStatus(effect)
    }
  }

  private fun loadNotifications(dispatchEvent: (QsPopupEvent) -> Unit) {
    notificationRepository
      .notifications()
      .onEach { dispatchEvent(NotificationsLoaded(it)) }
      .launchIn(this)
  }

  private suspend fun toggleNotificationPinStatus(effect: ToggleNotificationPinStatus) {
    val notification = effect.notification
    // We are doing is before to avoid waiting for the I/O
    // actions to be completed and also to avoid changing the
    // `toggleNotificationPinStatus` method to return the
    // notification.
    if (notification.isPinned) {
      // If already pinned, then dismiss the notification
      notificationUtil.dismissNotification(notification)
    } else {
      // If already is not pinned, then show the notification and pin it
      notificationUtil.showNotification(notification)
    }
    notificationRepository.toggleNotificationPinStatus(notification)
  }

}
