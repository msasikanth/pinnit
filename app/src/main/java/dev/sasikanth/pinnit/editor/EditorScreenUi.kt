package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.data.ScheduleType
import java.time.LocalDate
import java.time.LocalTime

interface EditorScreenUi {
  fun showScheduleView()
  fun renderScheduleDateTime(scheduleDate: LocalDate, scheduleTime: LocalTime)
  fun renderScheduleRepeat(scheduleType: ScheduleType?, hasValidScheduleResult: Boolean)
  fun hideScheduleView()
  fun showScheduleWarning()
  fun hideScheduleWarning()
  fun renderPinnitBottomBar(model: EditorScreenModel)
}
