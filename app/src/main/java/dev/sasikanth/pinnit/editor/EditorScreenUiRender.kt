package dev.sasikanth.pinnit.editor

class EditorScreenUiRender(private val ui: EditorScreenUi) {

  fun render(model: EditorScreenModel) {
    if (model.isNotificationLoaded) {
      ui.renderSaveActionButtonText()
      ui.showDeleteButton()
    } else {
      renderSaveButton(model)
      ui.hideDeleteButton()
    }

    if (model.hasNotificationTitle && (model.hasTitleAndContentChanged || model.hasScheduleChanged)) {
      ui.enableSave()
    } else {
      ui.disableSave()
    }

    renderScheduleView(model)
  }

  private fun renderSaveButton(model: EditorScreenModel) {
    if (model.hasSchedule) {
      ui.renderSaveActionButtonText()
    } else {
      ui.renderSaveAndPinActionButtonText()
    }
  }

  private fun renderScheduleView(model: EditorScreenModel) {
    if (model.hasSchedule) {
      val schedule = model.schedule!!

      ui.showScheduleView(
        scheduleDate = schedule.scheduleDate!!,
        scheduleTime = schedule.scheduleTime!!,
        scheduleType = schedule.scheduleType
      )
    } else {
      ui.hideScheduleView()
    }
  }
}
