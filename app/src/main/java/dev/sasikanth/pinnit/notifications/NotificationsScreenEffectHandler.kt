package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.functions.Consumer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.scheduler.PinnitNotificationScheduler
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class NotificationsScreenEffectHandler @AssistedInject constructor(
  private val notificationRepository: NotificationRepository,
  private val dispatcherProvider: DispatcherProvider,
  private val notificationUtil: NotificationUtil,
  private val pinnitNotificationScheduler: PinnitNotificationScheduler,
  @Assisted private val viewEffectConsumer: Consumer<NotificationScreenViewEffect>
) : CoroutineConnectable<NotificationsScreenEffect, NotificationsScreenEvent>(dispatcherProvider.main) {

  @AssistedFactory
  interface Factory {
    fun create(viewEffectConsumer: Consumer<NotificationScreenViewEffect>): NotificationsScreenEffectHandler
  }

  override suspend fun handler(effect: NotificationsScreenEffect, dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    when (effect) {
      LoadNotifications -> loadNotifications(dispatchEvent)

      CheckNotificationsVisibility -> checkNotificationsVisibility()

      is ToggleNotificationPinStatus -> toggleNotificationPinStatus(effect)

      is DeleteNotification -> deleteNotification(effect, dispatchEvent)

      is UndoDeletedNotification -> undoDeleteNotification(effect, dispatchEvent)

      is ShowUndoDeleteNotification -> showUndoDeleteNotification(effect)

      is CancelNotificationSchedule -> cancelNotificationSchedule(effect)

      is RemoveSchedule -> removeSchedule(effect, dispatchEvent)

      is ScheduleNotification -> pinnitNotificationScheduler.scheduleNotification(effect.notification)

      CheckPermissionToPostNotification -> checkPermissionToPostNotification(dispatchEvent)

      RequestNotificationPermission -> viewEffectConsumer.accept(RequestNotificationPermissionViewEffect)
    }
  }

  private fun checkPermissionToPostNotification(dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    val canPostNotifications = notificationUtil.hasPermissionToPostNotifications()
    dispatchEvent(HasPermissionToPostNotifications(canPostNotifications))
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
    notificationRepository.updatePinStatus(notification.uuid, !notification.isPinned)
  }

  private suspend fun deleteNotification(effect: DeleteNotification, dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    val deletedNotification = notificationRepository.deleteNotification(effect.notification)
    dispatchEvent(NotificationDeleted(deletedNotification))
  }

  private fun showUndoDeleteNotification(effect: ShowUndoDeleteNotification) {
    viewEffectConsumer.accept(UndoNotificationDeleteViewEffect(effect.notification.uuid))
  }

  private suspend fun undoDeleteNotification(effect: UndoDeletedNotification, dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    val notification = notificationRepository.notification(effect.notificationUuid)
    notificationRepository.undoNotificationDelete(notification)

    dispatchEvent(RestoredDeletedNotification(notification))
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

  private fun cancelNotificationSchedule(effect: CancelNotificationSchedule) {
    pinnitNotificationScheduler.cancel(effect.notificationId)
  }

  private suspend fun removeSchedule(effect: RemoveSchedule, dispatchEvent: (NotificationsScreenEvent) -> Unit) {
    val notificationId = effect.notificationId

    notificationRepository.removeSchedule(notificationId)

    dispatchEvent(RemovedNotificationSchedule(notificationId))
  }
}
