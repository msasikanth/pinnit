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

  @Test
  fun `when notification is clicked, then open notification editor`() {
    val notification = TestData.notification(
      uuid = UUID.fromString("0fdb4088-a12e-47e6-8d42-d5b553edd3b1"),
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )
    val notifications = listOf(notification)
    val model = defaultModel.onNotificationsLoaded(notifications)

    updateSpec
      .given(model)
      .whenEvent(NotificationClicked(notification))
      .then(
        UpdateSpec.assertThatNext(
          NextMatchers.hasNoModel(),
          NextMatchers.hasEffects(OpenNotificationEditor(notification) as QsPopupEffect)
        )
      )
  }

  @Test
  fun `when toggle notification pin status is clicked, then change the pin status`() {
    val notification = TestData.notification(
      uuid = UUID.fromString("0fdb4088-a12e-47e6-8d42-d5b553edd3b1"),
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )
    val notifications = listOf(notification)
    val model = defaultModel.onNotificationsLoaded(notifications)

    updateSpec
      .given(model)
      .whenEvent(TogglePinStatusClicked(notification))
      .then(
        UpdateSpec.assertThatNext(
          NextMatchers.hasNoModel(),
          NextMatchers.hasEffects(ToggleNotificationPinStatus(notification) as QsPopupEffect)
        )
      )
  }
}
