package dev.sasikanth.pinnit.notifications

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import com.spotify.mobius.Connection
import com.spotify.mobius.test.RecordingConsumer
import dev.sasikanth.pinnit.scheduler.PinnitNotificationScheduler
import dev.sasikanth.pinnit.utils.TestDispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

class NotificationsScreenEffectHandlerTest {

  private val utcClock = dev.sasikanth.sharedtestcode.utils.TestUtcClock().apply {
    setDate(LocalDate.parse("2020-02-14"))
  }

  private val testScope = TestCoroutineScope()

  private val consumer = RecordingConsumer<NotificationsScreenEvent>()
  private val viewActionsConsumer = RecordingConsumer<NotificationScreenViewEffect>()
  private lateinit var connection: Connection<NotificationsScreenEffect>

  private val notificationRepository = mock<NotificationRepository>()
  private val testDispatcherProvider = TestDispatcherProvider()
  private val notificationUtil = mock<NotificationUtil>()
  private val pinnitNotificationScheduler = mock<PinnitNotificationScheduler>()
  private val effectHandler = NotificationsScreenEffectHandler(
    notificationRepository,
    testDispatcherProvider,
    notificationUtil,
    pinnitNotificationScheduler,
    viewActionsConsumer
  )

  @Before
  fun setup() {
    connection = effectHandler.connect(consumer)
  }

  @After
  fun tearDown() {
    connection.dispose()
  }

  @Test
  fun `when load notifications effect is received, then fetch notifications`() {
    // given
    val notification1 = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = UUID.fromString("5d9e67ff-44ad-48c7-9ff8-69d7b927c175"),
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )
    val notification2 = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = UUID.fromString("f5570d1f-9054-4770-ae0a-6aacdb4c95b0"),
      createdAt = Instant.now(utcClock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(utcClock)
    )

    val notifications = listOf(notification1, notification2)
    val expectedNotifications = flowOf(notifications)

    whenever(notificationRepository.notifications()) doReturn expectedNotifications

    // when
    connection.accept(LoadNotifications)

    // then
    verify(notificationRepository).notifications()
    verifyNoMoreInteractions(notificationRepository)

    consumer.assertValues(NotificationsLoaded(notifications))
    viewActionsConsumer.assertValues()
  }

  @Test
  fun `when toggle pin status effect is received, then update the notification pin status`() = testScope.runBlockingTest {
    // given
    val notification = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = UUID.fromString("ff73fd70-852f-4833-bc9c-a6f67b2e66f0"),
      isPinned = false,
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )

    // when
    connection.accept(ToggleNotificationPinStatus(notification))

    // then
    verify(notificationRepository, times(1)).updatePinStatus(notification.uuid, true)
    verifyNoMoreInteractions(notificationRepository)

    verify(notificationUtil).showNotification(notification)
    verifyNoMoreInteractions(notificationUtil)

    consumer.assertValues()
    viewActionsConsumer.assertValues()
  }

  @Test
  fun `when toggle pin status effect is received and notification is pinned, then update the notification pin status`() = testScope.runBlockingTest {
    // given
    val notification = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = UUID.fromString("ff73fd70-852f-4833-bc9c-a6f67b2e66f0"),
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock),
      isPinned = true
    )

    // when
    connection.accept(ToggleNotificationPinStatus(notification))

    // then
    verify(notificationRepository, times(1)).updatePinStatus(notification.uuid, false)
    verifyNoMoreInteractions(notificationRepository)

    verify(notificationUtil).dismissNotification(notification)
    verifyNoMoreInteractions(notificationUtil)

    consumer.assertValues()
    viewActionsConsumer.assertValues()
  }

  @Test
  fun `when delete notification effect is received, then delete notification and show undo option`() = testScope.runBlockingTest {
    // given
    val notification = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = UUID.fromString("34727623-c572-455f-8e37-b1df3baca79e"),
      createdAt = Instant.now(utcClock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(utcClock)
    )

    val deletedNotification = notification.copy(
      deletedAt = Instant.now(utcClock).plus(10, ChronoUnit.MINUTES)
    )

    whenever(notificationRepository.deleteNotification(notification)) doReturn deletedNotification

    // when
    connection.accept(DeleteNotification(notification))

    // then
    verify(notificationRepository, times(1)).deleteNotification(notification)
    verifyNoMoreInteractions(notificationRepository)

    consumer.assertValues(NotificationDeleted(deletedNotification))
    viewActionsConsumer.assertValues()
  }

  @Test
  fun `when undo deleted notification effect is received, then undo the delete`() = testScope.runBlockingTest {
    // given
    val notificationUuid = UUID.fromString("f3d50ff2-5e92-4d46-b5b9-53bbe770ef9c")
    val notification = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = UUID.fromString("34727623-c572-455f-8e37-b1df3baca79e"),
      schedule = null,
      createdAt = Instant.now(utcClock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(utcClock),
      deletedAt = Instant.now(utcClock)
    )

    whenever(notificationRepository.notification(notificationUuid)) doReturn notification

    // when
    connection.accept(UndoDeletedNotification(notificationUuid))

    // then
    verify(notificationRepository).notification(notificationUuid)
    verify(notificationRepository).undoNotificationDelete(notification)
    verifyNoMoreInteractions(notificationRepository)

    consumer.assertValues(RestoredDeletedNotification(notification))
    viewActionsConsumer.assertValues()
  }

  @Test
  fun `when check notifications visibility effect is received, then check notifications visibility`() = testScope.runBlockingTest {
    // given
    val notification1 = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = UUID.fromString("199ec75d-938d-4481-97db-ba9124cb7d75"),
      title = "Notification 1",
      isPinned = true
    )

    val pinnedNotifications = listOf(notification1)

    whenever(notificationRepository.pinnedNotifications()) doReturn pinnedNotifications

    // when
    connection.accept(CheckNotificationsVisibility)

    // then
    verify(notificationRepository).pinnedNotifications()
    verifyNoMoreInteractions(notificationRepository)
    verify(notificationUtil).checkNotificationsVisibility(pinnedNotifications)
    verifyNoMoreInteractions(notificationUtil)

    consumer.assertValues()
    viewActionsConsumer.assertValues()
  }

  @Test
  fun `when show undo delete notification effect is received, then show the undo delete notification`() {
    // given
    val notification = dev.sasikanth.sharedtestcode.TestData.notification()

    // when
    connection.accept(ShowUndoDeleteNotification(notification))

    // then
    consumer.assertValues()
    viewActionsConsumer.assertValues(UndoNotificationDeleteViewEffect(notification.uuid))
  }

  @Test
  fun `when cancel schedule notification effect is received, then cancel the schedule`() {
    // give
    val notificationId = UUID.fromString("f249493f-7807-4e05-a3f8-dfdb049ad99f")

    // when
    connection.accept(CancelNotificationSchedule(notificationId))

    // then
    consumer.assertValues()
    viewActionsConsumer.assertValues()

    verify(pinnitNotificationScheduler).cancel(notificationId)
    verifyNoMoreInteractions(pinnitNotificationScheduler)
  }

  @Test
  fun `when remove schedule effect is received, then remove the schedule`() = testScope.runBlockingTest {
    // given
    val notificationId = UUID.fromString("b19bbdfd-786a-4228-a5cb-f12ab040ac07")

    // when
    connection.accept(RemoveSchedule(notificationId))

    // then
    consumer.assertValues(RemovedNotificationSchedule(notificationId))

    verify(notificationRepository).removeSchedule(notificationId)
    verifyNoMoreInteractions(notificationRepository)
  }

  @Test
  fun `when schedule notification effect is received, then schedule the notification`() {
    // given
    val notificationId = UUID.fromString("813b97a8-b323-4fcf-ac44-f8a543ae3c52")
    val notification = dev.sasikanth.sharedtestcode.TestData.notification(notificationId)

    // when
    connection.accept(ScheduleNotification(notification))

    // then
    consumer.assertValues()

    verify(pinnitNotificationScheduler).scheduleNotification(notification)
    verifyNoMoreInteractions(pinnitNotificationScheduler)
  }

  @Test
  fun `when check notifications permission effect is received, then check the permission`() {
    // given
    whenever(notificationUtil.hasPermissionToPostNotifications()) doReturn true

    // when
    connection.accept(CheckPermissionToPostNotification)

    // then
    consumer.assertValues(HasPermissionToPostNotifications(canPostNotifications = true))

    verify(notificationUtil).hasPermissionToPostNotifications()
    verifyNoMoreInteractions(notificationUtil)
  }

  @Test
  fun `when request notification permission effect is received, then request permission to post notifications`() {
    // when
    connection.accept(RequestNotificationPermission)

    // then
    consumer.assertValues()
    viewActionsConsumer.accept(RequestNotificationPermissionViewEffect)
  }
}
