package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.utils.TestUtcClock
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import java.util.UUID

class NotificationsScreenUpdateTest {

  @Test
  fun `when notifications are loaded, then update show notifications`() {
    val utcClock = TestUtcClock().apply {
      setDate(LocalDate.parse("2020-01-01"))
    }
    val updateSpec = UpdateSpec(NotificationsScreenUpdate())
    val defaultModel = NotificationsScreenModel.default()

    val notifications = listOf(
      TestData.notification(
        uuid = UUID.fromString("54651e1b-5de8-460a-8e3b-64e2e5aa70ac"),
        createdAt = Instant.now(utcClock),
        updatedAt = Instant.now(utcClock)
      )
    )

    updateSpec
      .given(defaultModel)
      .whenEvent(NotificationsLoaded(notifications))
      .then(
        assertThatNext(
          hasModel(defaultModel.onNotificationsLoaded(notifications)),
          hasNoEffects()
        )
      )
  }
}
