package dev.sasikanth.pinnit.notifications

import androidx.lifecycle.SavedStateHandle
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.runners.WorkRunner
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sasikanth.pinnit.utils.MAX_VIEW_EFFECTS_QUEUE_SIZE
import javax.inject.Inject

@HiltViewModel
class NotificationsScreenViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  update: NotificationsScreenUpdate,
  init: NotificationsScreenInit,
  effectHandlerFactory: NotificationsScreenEffectHandler.Factory,
  workRunner: WorkRunner
) : MobiusLoopViewModel<NotificationsScreenModel, NotificationsScreenEvent, NotificationsScreenEffect, NotificationScreenViewEffect>(
  { viewEffectsConsumer ->
    Mobius.loop(update, effectHandlerFactory.create(viewEffectsConsumer))
  },
  savedStateHandle.get<NotificationsScreenModel>(MODEL_KEY) ?: NotificationsScreenModel.default(),
  init,
  workRunner,
  MAX_VIEW_EFFECTS_QUEUE_SIZE
) {

  companion object {
    private const val MODEL_KEY = "NOTIFICATIONS_SCREEN_MODEL"
  }

  override fun onClearedInternal() {
    savedStateHandle[MODEL_KEY] = model
    super.onClearedInternal()
  }
}
