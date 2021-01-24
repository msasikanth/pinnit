package dev.sasikanth.pinnit.editor

import com.google.common.truth.Truth.assertThat
import dev.sasikanth.pinnit.editor.ScheduleValidator.Result.ScheduleInPastError
import dev.sasikanth.pinnit.editor.ScheduleValidator.Result.Valid
import dev.sasikanth.pinnit.utils.TestUserClock
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class ScheduleValidatorTest {

  private val userClock = TestUserClock(Instant.parse("2020-01-01T00:00:00.00Z"))
  private val validator = ScheduleValidator(userClock)

  @Test
  fun `when schedule is in future, then return schedule is valid`() {
    // given
    val scheduleDate = LocalDate.parse("2020-01-01")
    val scheduleTime = LocalTime.parse("09:00:00")

    // when
    val expectedResult = validator.validate(scheduleDate, scheduleTime)

    // then
    assertThat(expectedResult).isEqualTo(Valid)
  }

  @Test
  fun `when schedule is in past, then return schedule in past error`() {
    // given
    val scheduleDate = LocalDate.parse("2019-12-31")
    val scheduleTime = LocalTime.parse("09:00:00")

    // when
    val expectedResult = validator.validate(scheduleDate, scheduleTime)

    // then
    assertThat(expectedResult).isEqualTo(ScheduleInPastError)
  }
}
