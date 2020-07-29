package dev.sasikanth.pinnit.utils

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

// Source: https://github.com/simpledotorg/simple-android/blob/45d4b34019b31328b7593590ee5f1a2095638206/app/src/main/java/org/simple/clinic/util/Clocks.kt
open class UtcClock : Clock() {

  private val utcClock = systemUTC()

  override fun withZone(zoneId: ZoneId?): Clock = utcClock.withZone(zoneId)

  override fun getZone(): ZoneId = utcClock.zone

  override fun instant(): Instant = utcClock.instant()
}
