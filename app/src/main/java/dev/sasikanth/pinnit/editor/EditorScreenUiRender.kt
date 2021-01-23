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

    if (model.hasNotificationTitle && model.hasValidScheduleResult &&
      (model.hasTitleAndContentChanged || model.hasScheduleChanged)
    ) {
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

      ui.showScheduleView()
      ui.renderScheduleDateTime(scheduleDate = schedule.scheduleDate!!, scheduleTime = schedule.scheduleTime!!)
      ui.renderScheduleRepeat(scheduleType = schedule.scheduleType, hasValidScheduleResult = model.hasValidScheduleResult)

      if (!model.hasValidScheduleResult) {
        ui.showScheduleWarning()
      } else {
        ui.hideScheduleWarning()
      }
    } else {
      ui.hideScheduleView()
    }
  }
}
