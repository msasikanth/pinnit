package dev.sasikanth.pinnit.editor

import com.spotify.mobius.functions.Consumer
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil

class EditorScreenEffectHandler @AssistedInject constructor(
  private val notificationRepository: NotificationRepository,
  dispatcherProvider: DispatcherProvider,
  private val notificationUtil: NotificationUtil,
  @Assisted private val viewEffectConsumer: Consumer<EditorScreenViewEffect>
) : CoroutineConnectable<EditorScreenEffect, EditorScreenEvent>(dispatcherProvider.main) {

  @AssistedInject.Factory
  interface Factory {
    fun create(viewEffectConsumer: Consumer<EditorScreenViewEffect>): EditorScreenEffectHandler
  }

  override suspend fun handler(effect: EditorScreenEffect, dispatchEvent: (EditorScreenEvent) -> Unit) {
    when (effect) {
      is LoadNotification -> loadNotification(effect, dispatchEvent)

      is SetTitleAndContent -> setTitleAndContent(effect)

      is SaveNotificationAndCloseEditor -> saveNotificationAndCloseEditor(effect)

      is UpdateNotificationAndCloseEditor -> updateNotificationAndCloseEditor(effect)

      ShowConfirmExitEditor -> viewEffectConsumer.accept(ShowConfirmExitEditorDialog)

      CloseEditor -> viewEffectConsumer.accept(CloseEditorView)

      ShowConfirmDelete -> viewEffectConsumer.accept(ShowConfirmDeleteDialog)

      is DeleteNotification -> deleteNotification(effect)

      is ShowDatePicker -> viewEffectConsumer.accept(ShowDatePickerDialog(effect.date))
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

  private suspend fun saveNotificationAndCloseEditor(effect: SaveNotificationAndCloseEditor) {
    val notification = notificationRepository.save(effect.title, effect.content)
    notificationUtil.showNotification(notification)
    viewEffectConsumer.accept(CloseEditorView)
  }

  private suspend fun updateNotificationAndCloseEditor(effect: UpdateNotificationAndCloseEditor) {
    val notification = notificationRepository.notification(effect.notificationUuid)
    val updatedNotification = notificationRepository.updateNotification(
      notification.copy(
        title = effect.title,
        content = effect.content
      )
    )

    if (effect.showAndroidNotification) {
      notificationUtil.showNotification(updatedNotification)
    }
    viewEffectConsumer.accept(CloseEditorView)
  }

  private suspend fun deleteNotification(
    effect: DeleteNotification
  ) {
    val notification = effect.notification
    notificationRepository.toggleNotificationPinStatus(notification)
    notificationRepository.deleteNotification(notification)
    notificationUtil.dismissNotification(notification)
    viewEffectConsumer.accept(CloseEditorView)
  }
}
