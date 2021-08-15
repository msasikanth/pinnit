package dev.sasikanth.pinnit.editor

class EditorScreenUiRender(private val ui: EditorScreenUi) {

  fun render(model: EditorScreenModel) {
    ui.renderPinnitBottomBar(model)
    renderScheduleView(model)
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
