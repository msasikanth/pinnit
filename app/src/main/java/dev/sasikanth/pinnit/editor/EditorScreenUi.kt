package dev.sasikanth.pinnit.editor

interface EditorScreenUi {
  fun setTitle(notificationTitle: String)
  fun setContent(notificationContent: String?)
  fun enableSave()
}
