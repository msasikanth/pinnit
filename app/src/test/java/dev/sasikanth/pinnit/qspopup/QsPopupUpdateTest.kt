package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.test.NextMatchers
import com.spotify.mobius.test.UpdateSpec
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.utils.TestUtcClock
import org.junit.Test
import org.threeten.bp.Instant
import java.util.UUID

class QsPopupUpdateTest {

  private val utcClock = TestUtcClock()
  private val updateSpec = UpdateSpec(QsPopupUpdate())
  private val defaultModel = QsPopupModel.default()

  @Test
  fun `when notifications are loaded, then show notifications`() {
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
        UpdateSpec.assertThatNext(
          NextMatchers.hasModel(defaultModel.onNotificationsLoaded(notifications)),
          NextMatchers.hasNoEffects()
        )
      )
  }
}
