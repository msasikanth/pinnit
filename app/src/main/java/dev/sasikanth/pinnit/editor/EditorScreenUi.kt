package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.ScheduleType
import java.time.LocalDate
import java.time.LocalTime

interface EditorScreenUi {
  fun enableSave()
  fun disableSave()
  fun renderSaveActionButtonText()
  fun renderSaveAndPinActionButtonText()
  fun showDeleteButton()
  fun hideDeleteButton()
  fun showScheduleView()
  fun renderScheduleDateTime(scheduleDate: LocalDate, scheduleTime: LocalTime)
  fun renderScheduleRepeat(scheduleType: ScheduleType?, hasValidScheduleResult: Boolean)
  fun hideScheduleView()
  fun showScheduleWarning()
}
