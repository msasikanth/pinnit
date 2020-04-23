package dev.sasikanth.pinnit.notifications

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.sasikanth.pinnit.TestPinnitApp
import dev.sasikanth.pinnit.data.PinnitNotification
import dev.sasikanth.pinnit.utils.TestUtcClock
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
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
}
