package dev.sasikanth.pinnit.editor

import com.spotify.mobius.functions.Consumer
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.scheduler.PinnitNotificationScheduler
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil

class EditorScreenEffectHandler @AssistedInject constructor(
  private val notificationRepository: NotificationRepository,
  dispatcherProvider: DispatcherProvider,
  private val notificationUtil: NotificationUtil,
  private val pinnitNotificationScheduler: PinnitNotificationScheduler,
  @Assisted private val viewEffectConsumer: Consumer<EditorScreenViewEffect>
) : CoroutineConnectable<EditorScreenEffect, EditorScreenEvent>(dispatcherProvider.main) {

  @AssistedFactory
  interface Factory {
    fun create(viewEffectConsumer: Consumer<EditorScreenViewEffect>): EditorScreenEffectHandler
  }

  override suspend fun handler(effect: EditorScreenEffect, dispatchEvent: (EditorScreenEvent) -> Unit) {
    when (effect) {
      is LoadNotification -> loadNotification(effect, dispatchEvent)

      is SetTitleAndContent -> setTitleAndContent(effect)

      is SaveNotification -> saveNotification(effect, dispatchEvent)

      is UpdateNotification -> updateNotification(effect, dispatchEvent)

      ShowConfirmExitEditor -> viewEffectConsumer.accept(ShowConfirmExitEditorDialog)

      CloseEditor -> viewEffectConsumer.accept(CloseEditorView)

      ShowConfirmDelete -> viewEffectConsumer.accept(ShowConfirmDeleteDialog)

      is DeleteNotification -> deleteNotification(effect)

      is ShowDatePicker -> viewEffectConsumer.accept(ShowDatePickerDialog(effect.date))

      is ShowTimePicker -> viewEffectConsumer.accept(ShowTimePickerDialog(effect.time))

      is ShowNotification -> showNotification(effect)

      is ScheduleNotification -> scheduleNotification(effect)

      is CancelNotificationSchedule -> cancelNotificationSchedule(effect)
    }
  }

  private suspend fun loadNotification(
    effect: LoadNotification,
    dispatchEvent: (EditorScreenEvent) -> Unit
  ) {
    val notification = notificationRepository.notification(effect.uuid)
    dispatchEvent(NotificationLoaded(notification))
  }

  private fun setTitleAndContent(effect: SetTitleAndContent) {
    viewEffectConsumer.accept(SetTitle(effect.title))
    viewEffectConsumer.accept(SetContent(effect.content))
  }

  private suspend fun saveNotification(effect: SaveNotification, dispatchEvent: (EditorScreenEvent) -> Unit) {
    val notification = notificationRepository.save(
      title = effect.title,
      content = effect.content,
      isPinned = effect.canPinNotification,
      schedule = effect.schedule
    )
    dispatchEvent(NotificationSaved(notification))
  }

  private suspend fun updateNotification(effect: UpdateNotification, dispatchEvent: (EditorScreenEvent) -> Unit) {
    val notification = notificationRepository.notification(effect.notificationUuid)
    val updatedNotification = notificationRepository.updateNotification(
      notification.copy(
        title = effect.title,
        content = effect.content,
        schedule = effect.schedule
      )
    )

    dispatchEvent(NotificationUpdated(updatedNotification))
  }

  private suspend fun deleteNotification(
    effect: DeleteNotification
  ) {
    val notification = effect.notification
    notificationRepository.updatePinStatus(notification.uuid, false)
    notificationRepository.deleteNotification(notification)
    notificationUtil.dismissNotification(notification)
    viewEffectConsumer.accept(CloseEditorView)
    pinnitNotificationScheduler.cancel(notification.uuid)
  }

  private fun showNotification(effect: ShowNotification) {
    notificationUtil.showNotification(effect.notification)
  }

  private fun scheduleNotification(effect: ScheduleNotification) {
    pinnitNotificationScheduler.scheduleNotification(effect.notification)
  }

  private fun cancelNotificationSchedule(effect: CancelNotificationSchedule) {
    pinnitNotificationScheduler.cancel(effect.notificationId)
  }
}
