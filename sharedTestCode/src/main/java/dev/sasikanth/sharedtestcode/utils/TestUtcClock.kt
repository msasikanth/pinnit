package dev.sasikanth.sharedtestcode.utils

import dev.sasikanth.pinnit.utils.UtcClock
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset.UTC

class TestUtcClock(instant: Instant) : UtcClock() {

  constructor() : this(Instant.EPOCH)

  private var clock = fixed(instant, UTC)

  override fun withZone(zoneId: ZoneId?): Clock = clock.withZone(zoneId)

  override fun getZone(): ZoneId = UTC

  override fun instant(): Instant = clock.instant()

  fun setDate(date: LocalDate) {
    val instant = date.atStartOfDay(zone).toInstant()
    clock = fixed(instant, UTC)
  }

  fun advanceBy(duration: Duration) {
    clock = offset(clock, duration)
  }

  fun resetToEpoch() {
    clock = fixed(Instant.EPOCH, UTC)
  }
}
