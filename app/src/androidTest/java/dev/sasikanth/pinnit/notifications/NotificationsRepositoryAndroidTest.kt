package dev.sasikanth.pinnit.notifications

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.TestPinnitApp
import dev.sasikanth.pinnit.data.AppDatabase
import dev.sasikanth.pinnit.utils.TestUtcClock
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class NotificationsRepositoryAndroidTest {

  @Inject
  lateinit var notificationRepository: NotificationRepository

  @Inject
  lateinit var clock: TestUtcClock

  @Inject
  lateinit var appDatabase: AppDatabase

  @Before
  fun setup() {
    ApplicationProvider.getApplicationContext<TestPinnitApp>()
      .component
      .also { it.inject(this) }

    clock.setDate(LocalDate.parse("2020-02-14"))
  }

  @After
  fun teardown() {
    // TODO(SM-29 APR): Clear individual table
    // Since it's just one table right now, we can directly
    // clear the database tables. But it's better to clear
    // individual table in tests
    appDatabase.clearAllTables()
  }

  @Test
  fun saving_a_notification_should_work_correctly() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("76beb7bf-3036-430b-9a3d-fe41f9c5c4cb")
    val expectedNotification = TestData.notification(
      uuid = notificationUuid,
      title = "Notification Title",
      content = "Notification Content",
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock)
    )

    // when
    val savedNotification = notificationRepository.save(
      title = expectedNotification.title,
      content = expectedNotification.content,
      isPinned = expectedNotification.isPinned,
      schedule = expectedNotification.schedule,
      uuid = expectedNotification.uuid
    )

    // then
    assertThat(savedNotification).isEqualTo(expectedNotification)
  }

  @Test
  fun updating_notification_pin_status_should_work_correctly() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("76dde7cb-2d17-46c0-b523-3ea01eb1565e")
    val notification = TestData.notification(
      uuid = notificationUuid,
      title = "Notification Title",
      content = "Notification Content",
      isPinned = false,
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock)
    )

    // when
    notificationRepository.save(
      title = notification.title,
      content = notification.content,
      isPinned = notification.isPinned,
      schedule = notification.schedule,
      uuid = notification.uuid
    )
    notificationRepository.updatePinStatus(notificationUuid, true)

    // then
    val expectedNotification = notification.copy(
      isPinned = true
    )
    assertThat(notificationRepository.notification(notificationUuid)).isEqualTo(expectedNotification)
  }

  @Test
  fun getting_notifications_should_be_ordered_correctly() = runBlocking {
    // given
    val notificationNow = TestData.notification(
      uuid = UUID.fromString("57028af1-7a95-4015-9067-a9064d94f4c2"),
      title = "Notification Now",
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock)
    )

    val deletedNotification = TestData.notification(
      uuid = UUID.fromString("e0f54a14-64cf-49ae-9657-cbdea8cf33eb"),
      title = "Deleted Notification",
      isPinned = true,
      createdAt = Instant.now(clock).minus(5, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock).minus(5, ChronoUnit.DAYS),
      deletedAt = Instant.now(clock).minus(1, ChronoUnit.DAYS)
    )

    val pinnedNotification1 = TestData.notification(
      uuid = UUID.fromString("70525f53-3ed9-4053-ae35-93bf7f19dd24"),
      title = "Pinned Notification 1",
      isPinned = true,
      createdAt = Instant.now(clock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock).minus(1, ChronoUnit.DAYS)
    )

    val pinnedNotification2 = TestData.notification(
      uuid = UUID.fromString("d58aaa09-6b8a-4727-b9b5-30b15937594d"),
      title = "Pinned Notification 2",
      isPinned = true,
      createdAt = Instant.now(clock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock)
    )

    val notificationInPast10Min = TestData.notification(
      uuid = UUID.fromString("9083bdda-0d50-40ac-87e1-4627bdee294c"),
      title = "Notification created in past 10 minutes",
      createdAt = Instant.now(clock).minus(10, ChronoUnit.MINUTES),
      updatedAt = Instant.now(clock).minus(10, ChronoUnit.MINUTES)
    )

    val notificationUpdatedInPast5Min = TestData.notification(
      uuid = UUID.fromString("c40f0aee-5a5d-45e1-b54b-8b5d1e2a108e"),
      title = "Notification updated in past 5 minutes",
      createdAt = Instant.now(clock).minus(20, ChronoUnit.MINUTES),
      updatedAt = Instant.now(clock).minus(5, ChronoUnit.MINUTES)
    )

    // when
    notificationRepository.save(
      listOf(
        notificationNow,
        deletedNotification,
        pinnedNotification1,
        pinnedNotification2,
        notificationInPast10Min,
        notificationUpdatedInPast5Min
      )
    )

    // then
    val notifications = notificationRepository.notifications()

    // First it should be pinned notifications in DESC order based on updatedAt
    // Then it should be rest of the notifications excluding deleted ones in DESC order based on updatedAt
    notifications.test {
      listOf(
        pinnedNotification2,
        pinnedNotification1,
        notificationNow,
        notificationUpdatedInPast5Min,
        notificationInPast10Min
      )
    }
  }

  @Test
  fun updating_a_notification_should_work_correctly() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("3dd26618-66ad-4538-a9b7-50240af81535")
    val notification = TestData.notification(
      uuid = notificationUuid,
      title = "Original Title",
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock)
    )

    val updatedNotification = notification.copy(
      title = "Updated Title"
    )

    // when
    notificationRepository.save(listOf(notification))
    notificationRepository.updateNotification(updatedNotification)

    // then
    assertThat(notificationRepository.notification(notificationUuid))
      .isEqualTo(updatedNotification)
  }

  @Test
  fun deleting_a_notification_should_work_correctly() = runBlocking {
    // given
    val notification = TestData.notification(
      uuid = UUID.fromString("f6a082c4-0384-484f-94ca-80533b19cf47"),
      createdAt = Instant.now(clock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock).minus(1, ChronoUnit.DAYS)
    )

    val expectedDeletedNotification = notification.copy(
      deletedAt = Instant.now(clock)
    )

    // when
    val deletedNotification = notificationRepository.deleteNotification(notification)

    // then
    assertThat(deletedNotification).isEqualTo(expectedDeletedNotification)
  }

  @Test
  fun undo_a_notification_delete_should_work_correctly() = runBlocking {
    // given
    val notification = TestData.notification(
      uuid = UUID.fromString("f6a082c4-0384-484f-94ca-80533b19cf47"),
      createdAt = Instant.now(clock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock).minus(1, ChronoUnit.DAYS),
      deletedAt = Instant.now(clock)
    )

    val undidNotification = notification.copy(
      deletedAt = null
    )

    // when
    notificationRepository.undoNotificationDelete(notification)

    // then
    assertThat(notificationRepository.notification(notification.uuid))
      .isEqualTo(undidNotification)
  }

  @Test
  fun getting_pinned_notifications_should_work_correctly() = runBlocking {
    // given
    val pinnedNotificationInPast = TestData.notification(
      title = "Pinned notification in past",
      isPinned = true,
      createdAt = Instant.now(clock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock).minus(1, ChronoUnit.DAYS)
    )

    val notificationNow = TestData.notification(
      title = "Notification now",
      isPinned = false,
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock)
    )

    val pinnedNotificationNow = TestData.notification(
      title = "Pinned notification now",
      isPinned = true,
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock)
    )

    notificationRepository.save(listOf(pinnedNotificationInPast, notificationNow, pinnedNotificationNow))

    // when
    val pinnedNotifications = notificationRepository.pinnedNotifications()

    // then
    assertThat(pinnedNotifications)
      .isEqualTo(
        listOf(
          pinnedNotificationNow,
          pinnedNotificationInPast
        )
      )
  }

  @Test
  fun removing_a_schedule_from_notification_should_work_correctly() = runBlocking {
    // given
    val notification = TestData.notification(
      uuid = UUID.fromString("bd5e429c-d8cb-4e48-b2bf-784c38abea08"),
      schedule = TestData.schedule()
    )

    notificationRepository.save(listOf(notification))

    // when
    notificationRepository.removeSchedule(notification.uuid)

    // then
    val expectedNotification = notification.copy(schedule = null)
    assertThat(notificationRepository.notification(notification.uuid))
      .isEqualTo(expectedNotification)
  }
}
