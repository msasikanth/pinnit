package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.utils.ValueChangedCallback

class EditorScreenUiRender(private val ui: EditorScreenUi) {

  private val valueChangedCallback = ValueChangedCallback<EditorScreenModel>()

  fun render(model: EditorScreenModel) {
    if (model.title == null) {
      ui.renderSaveAndPinActionButtonText()
      return
    }

    valueChangedCallback.pass(model) {
      if (model.notification?.isPinned == true) {
        ui.renderSaveActionButtonText()
      } else {
        ui.renderSaveAndPinActionButtonText()
      }

      if (!model.title.isNullOrBlank()) {
        ui.enableSave()
      } else {
        ui.disableSave()
      }
    }
  }
}
