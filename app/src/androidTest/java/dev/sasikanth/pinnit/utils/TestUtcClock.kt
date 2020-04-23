package dev.sasikanth.pinnit.utils

import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset.UTC

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
