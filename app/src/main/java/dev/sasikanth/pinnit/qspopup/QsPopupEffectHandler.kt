package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.functions.Consumer
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import dev.sasikanth.pinnit.mobius.CoroutineConnectable
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.DispatcherProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class QsPopupEffectHandler @AssistedInject constructor(
  private val dispatcherProvider: DispatcherProvider,
  private val notificationRepository: NotificationRepository,
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
    }
  }

  private fun loadNotifications(dispatchEvent: (QsPopupEvent) -> Unit) {
    notificationRepository
      .notifications()
      .onEach { dispatchEvent(NotificationsLoaded(it)) }
      .launchIn(this)
  }
}
