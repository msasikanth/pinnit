package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.utils.TestUtcClock
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class NotificationsScreenUpdateTest {

  private val utcClock = TestUtcClock().apply {
    setDate(LocalDate.parse("2020-01-01"))
  }
  private val updateSpec = UpdateSpec(NotificationsScreenUpdate())
  private val defaultModel = NotificationsScreenModel.default()

  @Test
  fun `when notifications are loaded, then update show notifications`() {
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

  @Test
  fun `when notification is swiped, then delete the notification`() {
    val notification = TestData.notification(
      uuid = UUID.fromString("0fdb4088-a12e-47e6-8d42-d5b553edd3b1"),
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )
    val notifications = listOf(notification)
    val model = defaultModel.onNotificationsLoaded(notifications)

    updateSpec
      .given(model)
      .whenEvent(NotificationSwiped(notification))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(DeleteNotification(notification) as NotificationsScreenEffect)
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
          hasEffects(ToggleNotificationPinStatus(notification) as NotificationsScreenEffect)
        )
      )
  }

  @Test
  fun `when undo delete is clicked, then undo deleted notification`() {
    val notification = TestData.notification(
      uuid = UUID.fromString("0fdb4088-a12e-47e6-8d42-d5b553edd3b1"),
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock),
      deletedAt = Instant.now(utcClock)
    )
    val notifications = listOf(notification)
    val model = defaultModel.onNotificationsLoaded(notifications)

    updateSpec
      .given(model)
      .whenEvent(UndoNotificationDelete(notification.uuid))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(UndoDeletedNotification(notification.uuid) as NotificationsScreenEffect)
        )
      )
  }

  @Test
  fun `when notification is deleted, then show undo delete notification`() {
    val notification = TestData.notification(
      schedule = null
    )
    updateSpec
      .given(defaultModel)
      .whenEvent(NotificationDeleted(notification))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowUndoDeleteNotification(notification) as NotificationsScreenEffect)
        )
      )
  }

  @Test
  fun `when notification is deleted and has schedule, then cancel the schedule`() {
    val notification = TestData.notification(
      schedule = TestData.schedule()
    )
    updateSpec
      .given(defaultModel)
      .whenEvent(NotificationDeleted(notification))
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(ShowUndoDeleteNotification(notification), CancelNotificationSchedule(notification.uuid))
        )
      )
  }

  @Test
  fun `when notification schedule is removed, then cancel the notification schedule`() {
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
  fun `when remove notification schedule is clicked, then remove notification schedule`() {
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
}
