package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.test.NextMatchers
import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.utils.TestUtcClock
import org.junit.Test
import java.time.Instant
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
        assertThatNext(
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
        assertThatNext(
          hasNoModel(),
          hasEffects(OpenNotificationEditor(notification) as QsPopupEffect)
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
        assertThatNext(
          hasNoModel(),
          hasEffects(ToggleNotificationPinStatus(notification) as QsPopupEffect)
        )
      )
  }

  @Test
  fun `when schedule is removed, then cancel the notification schedule`() {
    val notification = TestData.notification(
      schedule = TestData.schedule()
    )

    updateSpec
      .given(defaultModel)
      .whenEvent(RemovedNotificationSchedule(notification.uuid))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(CancelNotificationSchedule(notification.uuid))
        )
      )
  }

  @Test
  fun `when remove notification schedule is clicked, then remove schedule`() {
    val notification = TestData.notification(
      schedule = TestData.schedule()
    )

    updateSpec
      .given(defaultModel)
      .whenEvent(RemoveNotificationScheduleClicked(notification.uuid))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(RemoveSchedule(notification.uuid))
        )
      )
  }

  @Test
  fun `when edit notification schedule is clicked, then open notification editor`() {
    val notification = TestData.notification(
      schedule = TestData.schedule()
    )

    updateSpec
      .given(defaultModel)
      .whenEvent(EditNotificationScheduleClicked(notification))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(OpenNotificationEditor(notification))
        )
      )
  }
}
