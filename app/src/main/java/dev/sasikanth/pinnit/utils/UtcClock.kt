package dev.sasikanth.pinnit.utils

import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

open class UtcClock : Clock() {

  private val utcClock = systemUTC()

  override fun withZone(zoneId: ZoneId?): Clock = utcClock.withZone(zoneId)

  override fun getZone(): ZoneId = utcClock.zone

  override fun instant(): Instant = utcClock.instant()
}
