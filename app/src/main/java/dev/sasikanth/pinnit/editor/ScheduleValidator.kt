package dev.sasikanth.pinnit.editor

import dev.sasikanth.pinnit.editor.ScheduleValidator.Result.ScheduleInPastError
import dev.sasikanth.pinnit.editor.ScheduleValidator.Result.Valid
import dev.sasikanth.pinnit.utils.UserClock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

class ScheduleValidator @Inject constructor(val userClock: UserClock) {

  fun validate(scheduleDate: LocalDate, scheduleTime: LocalTime): Result {
    val scheduleDateTime = scheduleDate.atTime(scheduleTime)
    val now = LocalDateTime.now(userClock)

    if (scheduleDateTime.isBefore(now)) {
      return ScheduleInPastError
    }

    return Valid
  }

  sealed class Result {

    object Valid : Result()

    object ScheduleInPastError : Result()
  }
}
