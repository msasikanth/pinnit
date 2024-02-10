package dev.sasikanth.pinnit.qspopup

import androidx.lifecycle.SavedStateHandle
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.runners.WorkRunner
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sasikanth.pinnit.utils.MAX_VIEW_EFFECTS_QUEUE_SIZE
import javax.inject.Inject

@HiltViewModel
class QsPopupViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  update: QsPopupUpdate,
  init: QsPopupInit,
  effectHandlerFactory: QsPopupEffectHandler.Factory,
  workRunner: WorkRunner
) : MobiusLoopViewModel<QsPopupModel, QsPopupEvent, QsPopupEffect, QsPopupViewEffect>(
  { viewEffectsConsumer ->
    Mobius.loop(update, effectHandlerFactory.create(viewEffectsConsumer))
  },
  savedStateHandle.get<QsPopupModel>(MODEL_KEY) ?: QsPopupModel.default(),
  init,
  workRunner,
  MAX_VIEW_EFFECTS_QUEUE_SIZE
) {

  companion object {
    private const val MODEL_KEY = "QS_POPUP_MODEL"
  }

  override fun onClearedInternal() {
    savedStateHandle[MODEL_KEY] = model
    super.onClearedInternal()
  }
}
