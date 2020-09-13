package dev.sasikanth.pinnit.editor

class EditorScreenUiRender(private val ui: EditorScreenUi) {

  fun render(model: EditorScreenModel) {
    if (model.isNotificationLoaded) {
      ui.renderSaveActionButtonText()
      ui.showDeleteButton()
    } else {
      ui.renderSaveAndPinActionButtonText()
      ui.hideDeleteButton()
    }

    if (!model.title.isNullOrBlank()) {
      ui.enableSave()
    } else {
      ui.disableSave()
    }
  }
}
