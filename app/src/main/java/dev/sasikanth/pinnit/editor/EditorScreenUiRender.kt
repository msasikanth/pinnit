package dev.sasikanth.pinnit.editor

class EditorScreenUiRender(private val ui: EditorScreenUi) {

  fun render(model: EditorScreenModel) {
    if (model.isNotificationPinned) {
      ui.renderSaveActionButtonText()
    } else {
      ui.renderSaveAndPinActionButtonText()
    }

    if (model.isNotificationLoaded) {
      ui.showDeleteButton()
    } else {
      ui.hideDeleteButton()
    }

    if (!model.title.isNullOrBlank()) {
      ui.enableSave()
    } else {
      ui.disableSave()
    }
  }
}
