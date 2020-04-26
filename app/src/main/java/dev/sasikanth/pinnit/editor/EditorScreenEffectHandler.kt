package dev.sasikanth.pinnit.editor

import com.spotify.mobius.functions.Consumer
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import javax.inject.Inject

class EditorScreenEffectHandler @Inject constructor(
  private val notificationRepository: NotificationRepository,
  private val dispatcherProvider: DispatcherProvider,
  private val viewEffectConsumer: Consumer<EditorScreenViewEffect>
) : CoroutineConnectable<EditorScreenEffect, EditorScreenEvent>(dispatcherProvider.main) {

  override suspend fun handler(effect: EditorScreenEffect, dispatchEvent: (EditorScreenEvent) -> Unit) {
    when (effect) {
      is LoadNotification -> {
        val notification = notificationRepository.notification(effect.uuid)
        dispatchEvent(NotificationLoaded(notification))
      }

      is SaveNotificationAndCloseEditor -> {
        notificationRepository.save(effect.title, effect.content)
        viewEffectConsumer.accept(CloseEditor)
      }
    }
  }
}
