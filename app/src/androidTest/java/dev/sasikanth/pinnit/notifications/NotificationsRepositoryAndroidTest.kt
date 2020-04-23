package dev.sasikanth.pinnit.notifications

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.sasikanth.pinnit.TestPinnitApp
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.utils.TestUtcClock
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class NotificationsRepositoryAndroidTest {

  @Inject
  lateinit var notificationRepository: NotificationRepository

  @Inject
  lateinit var clock: TestUtcClock

  @Before
  fun setup() {
    ApplicationProvider.getApplicationContext<TestPinnitApp>()
      .component
      .also { it.inject(this) }

    clock.setDate(LocalDate.parse("2020-02-14"))
  }

  @Test
  fun saving_a_notification_should_work_correctly() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("76beb7bf-3036-430b-9a3d-fe41f9c5c4cb")
    val expectedNotification = PinnitNotification(
      uuid = notificationUuid,
      title = "Notification Title",
      content = "Notification Content",
      isPinned = false,
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock),
      deletedAt = null
    )

    // when
    val savedNotification = notificationRepository.save(
      title = expectedNotification.title,
      content = expectedNotification.content,
      isPinned = expectedNotification.isPinned,
      uuid = expectedNotification.uuid
    )

    // then
    assertThat(savedNotification).isEqualTo(expectedNotification)
  }

  @Test
  fun toggling_notification_pin_status_should_work_correctly() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("76dde7cb-2d17-46c0-b523-3ea01eb1565e")
    val notification = PinnitNotification(
      uuid = notificationUuid,
      title = "Notification Title",
      content = "Notification Content",
      isPinned = false,
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock),
      deletedAt = null
    )

    // when
    notificationRepository.save(
      title = notification.title,
      content = notification.content,
      isPinned = notification.isPinned,
      uuid = notification.uuid
    )
    notificationRepository.toggleNotificationPinStatus(notification)

    // then
    val expectedNotification = notification.copy(
      isPinned = true
    )
    assertThat(notificationRepository.notification(notificationUuid)).isEqualTo(expectedNotification)
  }

  @Test
  fun getting_notifications_should_be_ordered_correctly() = runBlocking {
    // given
    val notificationNow = PinnitNotification(
      uuid = UUID.fromString("57028af1-7a95-4015-9067-a9064d94f4c2"),
      title = "Notification Now",
      isPinned = false,
      createdAt = Instant.now(clock),
      updatedAt = Instant.now(clock)
    )

    val deletedNotification = PinnitNotification(
      uuid = UUID.fromString("e0f54a14-64cf-49ae-9657-cbdea8cf33eb"),
      title = "Deleted Notification",
      isPinned = true,
      createdAt = Instant.now(clock).minus(5, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock).minus(5, ChronoUnit.DAYS),
      deletedAt = Instant.now(clock).minus(1, ChronoUnit.DAYS)
    )

    val pinnedNotification1 = PinnitNotification(
      uuid = UUID.fromString("70525f53-3ed9-4053-ae35-93bf7f19dd24"),
      title = "Pinned Notification 1",
      isPinned = true,
      createdAt = Instant.now(clock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock).minus(1, ChronoUnit.DAYS)
    )

    val pinnedNotification2 = PinnitNotification(
      uuid = UUID.fromString("d58aaa09-6b8a-4727-b9b5-30b15937594d"),
      title = "Pinned Notification 2",
      isPinned = true,
      createdAt = Instant.now(clock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(clock)
    )

    val notificationInPast10Min = PinnitNotification(
      uuid = UUID.fromString("9083bdda-0d50-40ac-87e1-4627bdee294c"),
      title = "Notification created in past 10 minutes",
      isPinned = false,
      createdAt = Instant.now(clock).minus(10, ChronoUnit.MINUTES),
      updatedAt = Instant.now(clock).minus(10, ChronoUnit.MINUTES)
    )

    val notificationUpdatedInPast5Min = PinnitNotification(
      uuid = UUID.fromString("c40f0aee-5a5d-45e1-b54b-8b5d1e2a108e"),
      title = "Notification updated in past 5 minutes",
      isPinned = false,
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
    val notifications = notificationRepository.notifications().take(1).toList().flatten()

    // First it should be pinned notifications in DESC order based on updatedAt
    // Then it should be rest of the notifications excluding deleted ones in DESC order based on updatedAt
    assertThat(notifications)
      .isEqualTo(
        listOf(
          pinnedNotification2,
          pinnedNotification1,
          notificationNow,
          notificationUpdatedInPast5Min,
          notificationInPast10Min
        )
      )
  }

  @Test
  fun updating_a_notification_should_work_correctly() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("3dd26618-66ad-4538-a9b7-50240af81535")
    val notification = PinnitNotification(
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
}
