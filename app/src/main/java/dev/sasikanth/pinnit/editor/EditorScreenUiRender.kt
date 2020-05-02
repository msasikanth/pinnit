package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.utils.ValueChangedCallback

class EditorScreenUiRender(private val ui: EditorScreenUi) {

  private val valueChangedCallback = ValueChangedCallback<EditorScreenModel>()

  fun render(model: EditorScreenModel) {
    valueChangedCallback.pass(model) {
      if (model.isNotificationPinned) {
        ui.renderSaveActionButtonText()
      } else {
        ui.renderSaveAndPinActionButtonText()
      }

      if (model.isNotificationLoaded) {
        ui.showDeleteButton()
      }

      if (!model.title.isNullOrBlank()) {
        ui.enableSave()
      } else {
        ui.disableSave()
      }
    }
  }
}
