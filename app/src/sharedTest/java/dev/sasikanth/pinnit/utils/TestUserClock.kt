package dev.sasikanth.pinnit.utils

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC

class TestUserClock(instant: Instant) : UserClock() {

  constructor() : this(Instant.EPOCH)

  constructor(localDate: LocalDate, zoneOffset: ZoneOffset = UTC) : this(instantFromDateAtZone(localDate, zoneOffset))

  private var clock = fixed(instant, UTC)

  override fun withZone(zone: ZoneId): Clock = clock.withZone(zone)

  override fun getZone(): ZoneId = clock.zone

  override fun instant(): Instant = clock.instant()

  fun advanceBy(duration: Duration) {
    clock = offset(clock, duration)
  }

  fun setDate(date: LocalDate, zone: ZoneId = this.zone) {
    val instant = instantFromDateAtZone(date, zone)
    clock = fixed(instant, zone)
  }
}

private fun instantFromDateAtZone(localDate: LocalDate, zone: ZoneId): Instant {
  return localDate.atStartOfDay(zone).toInstant()
}
