package dev.sasikanth.pinnit.editor

interface EditorScreenUi {
  fun enableSave()
  fun disableSave()
  fun renderSaveActionButtonText()
  fun renderSaveAndPinActionButtonText()
  fun showDeleteButton()
  fun hideDeleteButton()
}
