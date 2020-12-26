package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.functions.Consumer
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class NotificationsScreenEffectHandler @AssistedInject constructor(
  private val notificationRepository: NotificationRepository,
  private val dispatcherProvider: DispatcherProvider,
  private val notificationUtil: NotificationUtil,
  @Assisted private val viewEffectConsumer: Consumer<NotificationScreenViewEffect>
) : CoroutineConnectable<NotificationsScreenEffect, NotificationsScreenEvent>(dispatcherProvider.main) {

  @AssistedInject.Factory
  interface Factory {
    fun create(viewEffectConsumer: Consumer<NotificationScreenViewEffect>): NotificationsScreenEffectHandler
  }

  override suspend fun handler(effect: NotificationsScreenEffect, dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    when (effect) {
      LoadNotifications -> loadNotifications(dispatchEvent)

      CheckNotificationsVisibility -> checkNotificationsVisibility()

      is ToggleNotificationPinStatus -> toggleNotificationPinStatus(effect)

      is DeleteNotification -> deleteNotification(effect)

      is UndoDeletedNotification -> undoDeleteNotification(effect)

      is ShowUndoDeleteNotification -> showUndoDeleteNotification(effect)
    }
  }

  private fun loadNotifications(dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    val notificationsFlow = notificationRepository.notifications()
    notificationsFlow
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

  private suspend fun deleteNotification(effect: DeleteNotification) {
    notificationRepository.deleteNotification(effect.notification)
    viewEffectConsumer.accept(UndoNotificationDeleteViewEffect(effect.notification.uuid))
  }

  private fun showUndoDeleteNotification(effect: ShowUndoDeleteNotification) {
    viewEffectConsumer.accept(UndoNotificationDeleteViewEffect(effect.notification.uuid))
  }

  private suspend fun undoDeleteNotification(effect: UndoDeletedNotification) {
    val notification = notificationRepository.notification(effect.notificationUuid)
    notificationRepository.undoNotificationDelete(notification)
  }

  /**
   * We will check for notifications visibility only at start, so it's fine
   * to get the Flow as list.
   */
  private suspend fun checkNotificationsVisibility() {
    val notifications = notificationRepository.pinnedNotifications()
    withContext(dispatcherProvider.default) {
      notificationUtil.checkNotificationsVisibility(notifications)
    }
  }
}
