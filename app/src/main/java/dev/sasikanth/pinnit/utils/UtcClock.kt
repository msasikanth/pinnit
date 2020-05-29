package dev.sasikanth.pinnit.utils

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

open class UtcClock : Clock() {

  private val utcClock = systemUTC()

  override fun withZone(zoneId: ZoneId?): Clock = utcClock.withZone(zoneId)

  override fun getZone(): ZoneId = utcClock.zone

  override fun instant(): Instant = utcClock.instant()
}
