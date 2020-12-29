package dev.sasikanth.pinnit.utils

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

// Source: https://github.com/simpledotorg/simple-android/blob/45d4b34019b31328b7593590ee5f1a2095638206/app/src/main/java/org/simple/clinic/util/Clocks.kt
abstract class UserClock : Clock()

class RealUserClock(private val userTimeZone: ZoneId) : UserClock() {

  private val userClock = system(userTimeZone)

  override fun getZone(): ZoneId = userTimeZone

  override fun withZone(zoneId: ZoneId?): Clock = userClock.withZone(zoneId)

  override fun instant(): Instant = userClock.instant()
}
