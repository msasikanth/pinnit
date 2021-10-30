package dev.sasikanth.pinnit.editor

import androidx.lifecycle.SavedStateHandle
import com.spotify.mobius.Mobius
import com.spotify.mobius.android.MobiusLoopViewModel
import com.spotify.mobius.runners.WorkRunner
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sasikanth.pinnit.utils.MAX_VIEW_EFFECTS_QUEUE_SIZE
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditorScreenViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  update: EditorScreenUpdate,
  init: EditorScreenInit,
  effectHandlerFactory: EditorScreenEffectHandler.Factory,
  workRunner: WorkRunner
) : MobiusLoopViewModel<EditorScreenModel, EditorScreenEvent, EditorScreenEffect, EditorScreenViewEffect>(
  { viewEffectsConsumer ->
    Mobius.loop(update, effectHandlerFactory.create(viewEffectsConsumer))
  },
  editorScreenModel(savedStateHandle),
  init,
  workRunner,
  MAX_VIEW_EFFECTS_QUEUE_SIZE
) {

  companion object {
    private const val MODEL_KEY = "EDITOR_SCREEN_MODEL"

    private fun editorScreenModel(savedStateHandle: SavedStateHandle): EditorScreenModel {
      val args = EditorScreenArgs.fromSavedStateHandle(savedStateHandle)

      return savedStateHandle.get<EditorScreenModel>(MODEL_KEY) ?: EditorScreenModel.default(
        notificationUuid = args.notificationUuid,
        title = args.notificationTitle,
        content = args.notificationContent
      )
    }
  }

  override fun onClearedInternal() {
    savedStateHandle.set(MODEL_KEY, model)
    super.onClearedInternal()
  }
}
