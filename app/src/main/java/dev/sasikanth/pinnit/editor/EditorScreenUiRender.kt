package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.utils.ValueChangedCallback

class EditorScreenUiRender(private val ui: EditorScreenUi) {

  private val valueChangedCallback = ValueChangedCallback<EditorScreenModel>()

  fun render(model: EditorScreenModel) {
    if (model.title == null) {
      return
    }

    valueChangedCallback.pass(model) {
      if (!model.title.isNullOrBlank()) {
        ui.enableSave()
      } else {
        ui.disableSave()
      }
    }
  }
}
