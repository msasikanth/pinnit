package dev.sasikanth.pinnit.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PinnitDateTimeFormatterTest {

  private val dateFormatter = DateTimeFormatter.ofPattern("MMM d")
  private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

  @Test
  fun getting_2_sec_relative_timestamp_should_work_correctly() {
    // given
    val userClock = TestUserClock(instant = Instant.parse("2018-01-01T00:00:02Z"))
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedRelativeTimeStamp = "Now"
    val time = Instant.parse("2018-01-01T00:00:00Z")

    // when
    val actualRelativeTimeStamp = pinnitDateTimeFormatter.relativeTimeStamp(time)

    // then
    assertThat(actualRelativeTimeStamp).isEqualTo(expectedRelativeTimeStamp)
  }

  @Test
  fun getting_4_minutes_ago_relative_timestamp_should_work_correctly() {
    // given
    val userClock = TestUserClock(instant = Instant.parse("2018-01-01T00:04:02Z"))
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedRelativeTimeStamp = "4m ago"
    val time = Instant.parse("2018-01-01T00:00:00Z")

    // when
    val actualRelativeTimeStamp = pinnitDateTimeFormatter.relativeTimeStamp(time)

    // then
    assertThat(actualRelativeTimeStamp).isEqualTo(expectedRelativeTimeStamp)
  }

  @Test
  fun getting_4_hours_ago_relative_timestamp_should_work_correctly() {
    // given
    val userClock = TestUserClock(instant = Instant.parse("2018-01-01T04:04:02Z"))
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedRelativeTimeStamp = "4H ago"
    val time = Instant.parse("2018-01-01T00:00:00Z")

    // when
    val actualRelativeTimeStamp = pinnitDateTimeFormatter.relativeTimeStamp(time)

    // then
    assertThat(actualRelativeTimeStamp).isEqualTo(expectedRelativeTimeStamp)
  }

  @Test
  fun getting_yesterday_relative_timestamp_should_work_correctly() {
    // given
    val userClock = TestUserClock(instant = Instant.parse("2018-01-02T04:04:02Z"))
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedRelativeTimeStamp = "Yesterday"
    val time = Instant.parse("2018-01-01T00:00:00Z")

    // when
    val actualRelativeTimeStamp = pinnitDateTimeFormatter.relativeTimeStamp(time)

    // then
    assertThat(actualRelativeTimeStamp).isEqualTo(expectedRelativeTimeStamp)
  }

  @Test
  fun getting_2_days_old_relative_timestamp_should_work_correctly() {
    // given
    val userClock = TestUserClock(instant = Instant.parse("2018-01-03T04:04:02Z"))
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedRelativeTimeStamp = "2D ago"
    val time = Instant.parse("2018-01-01T00:00:00Z")

    // when
    val actualRelativeTimeStamp = pinnitDateTimeFormatter.relativeTimeStamp(time)

    // then
    assertThat(actualRelativeTimeStamp).isEqualTo(expectedRelativeTimeStamp)
  }

  @Test
  fun getting_today_date_time_string_should_work_correctly() {
    // given
    val userClock = TestUserClock(
      localDate = LocalDate.parse("2018-01-01")
    )
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedTodayDateTimeString = "Today - 4:04 AM"
    val scheduleDate = LocalDate.parse("2018-01-01")
    val scheduleTime = LocalTime.parse("04:04:00")

    // when
    val actualTodayDateTimeString = pinnitDateTimeFormatter.dateAndTimeString(
      date = scheduleDate,
      time = scheduleTime
    )

    // then
    assertThat(actualTodayDateTimeString).isEqualTo(expectedTodayDateTimeString)
  }

  @Test
  fun getting_yesterday_date_time_string_should_work_correctly() {
    // given
    val userClock = TestUserClock(
      localDate = LocalDate.parse("2018-01-01")
    )
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedTodayDateTimeString = "Yesterday - 4:04 AM"
    val scheduleDate = LocalDate.parse("2017-12-31")
    val scheduleTime = LocalTime.parse("04:04:00")

    // when
    val actualTodayDateTimeString = pinnitDateTimeFormatter.dateAndTimeString(
      date = scheduleDate,
      time = scheduleTime
    )

    // then
    assertThat(actualTodayDateTimeString).isEqualTo(expectedTodayDateTimeString)
  }

  @Test
  fun getting_tomorrow_date_time_string_should_work_correctly() {
    // given
    val userClock = TestUserClock(
      localDate = LocalDate.parse("2018-01-01")
    )
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedTodayDateTimeString = "Tomorrow - 4:04 AM"
    val scheduleDate = LocalDate.parse("2018-01-02")
    val scheduleTime = LocalTime.parse("04:04:00")

    // when
    val actualTodayDateTimeString = pinnitDateTimeFormatter.dateAndTimeString(
      date = scheduleDate,
      time = scheduleTime
    )

    // then
    assertThat(actualTodayDateTimeString).isEqualTo(expectedTodayDateTimeString)
  }

  @Test
  fun getting_date_time_string_should_work_correctly() {
    // given
    val userClock = TestUserClock(
      localDate = LocalDate.parse("2018-01-01")
    )
    val pinnitDateTimeFormatter = PinnitDateTimeFormatter(
      userClock = userClock,
      dateFormatter = dateFormatter,
      timeFormatter = timeFormatter
    )
    val expectedTodayDateTimeString = "Jan 6 - 4:04 AM"
    val scheduleDate = LocalDate.parse("2018-01-06")
    val scheduleTime = LocalTime.parse("04:04:00")

    // when
    val actualTodayDateTimeString = pinnitDateTimeFormatter.dateAndTimeString(
      date = scheduleDate,
      time = scheduleTime
    )

    // then
    assertThat(actualTodayDateTimeString).isEqualTo(expectedTodayDateTimeString)
  }
}
