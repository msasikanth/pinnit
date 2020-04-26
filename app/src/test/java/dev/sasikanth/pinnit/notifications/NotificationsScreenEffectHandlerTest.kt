package dev.sasikanth.pinnit.notifications

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.spotify.mobius.Connection
import com.spotify.mobius.test.RecordingConsumer
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.utils.TestDispatcherProvider
import dev.sasikanth.pinnit.utils.TestUtcClock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.UUID

class NotificationsScreenEffectHandlerTest {

  private val utcClock = TestUtcClock().apply {
    setDate(LocalDate.parse("2020-02-14"))
  }

  private val consumer = RecordingConsumer<NotificationsScreenEvent>()
  private val viewActionsConsumer = RecordingConsumer<NotificationScreenViewEffect>()
  private lateinit var connection: Connection<NotificationsScreenEffect>

  private val notificationRepository = mock<NotificationRepository>()
  private val testDispatcherProvider = TestDispatcherProvider()
  private val effectHandler = NotificationsScreenEffectHandler(
    notificationRepository,
    testDispatcherProvider,
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
    val notification1 = TestData.notification(
      uuid = UUID.fromString("5d9e67ff-44ad-48c7-9ff8-69d7b927c175"),
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )
    val notification2 = TestData.notification(
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
  fun `when open edit screen effect is received, then open the edit page`() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("220c2037-94ba-44ef-8f83-5c232f01288f")
    val notification = TestData.notification(
      uuid = notificationUuid,
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )

    whenever(notificationRepository.notification(notificationUuid)) doReturn notification

    // when
    connection.accept(OpenNotificationEditor(notificationUuid))

    // then
    verifyZeroInteractions(notificationRepository)

    consumer.assertValues()
    viewActionsConsumer.assertValues(OpenNotificationEditorViewEffect(notificationUuid))
  }

  @Test
  fun `when toggle pin status effect is received, then update the notification pin status`() = runBlocking {
    // given
    val notification = TestData.notification(
      uuid = UUID.fromString("ff73fd70-852f-4833-bc9c-a6f67b2e66f0"),
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )

    // when
    connection.accept(ToggleNotificationPinStatus(notification))

    // then
    verify(notificationRepository, times(1)).toggleNotificationPinStatus(notification)
    verifyNoMoreInteractions(notificationRepository)

    consumer.assertValues()
    viewActionsConsumer.assertValues()
  }

  @Test
  fun `when delete notification effect is received, then delete notification and show undo option`() = runBlocking {
    // given
    val notification = TestData.notification(
      uuid = UUID.fromString("34727623-c572-455f-8e37-b1df3baca79e"),
      createdAt = Instant.now(utcClock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(utcClock)
    )

    // when
    connection.accept(DeleteNotification(notification))

    // then
    verify(notificationRepository, times(1)).deleteNotification(notification)
    verifyNoMoreInteractions(notificationRepository)

    consumer.assertValues()
    viewActionsConsumer.assertValues(UndoNotificationDeleteViewEffect(notification.uuid))
  }

  @Test
  fun `when undo deleted notification effect is received, then undo the delete`() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("f3d50ff2-5e92-4d46-b5b9-53bbe770ef9c")
    val notification = TestData.notification(
      uuid = UUID.fromString("34727623-c572-455f-8e37-b1df3baca79e"),
      createdAt = Instant.now(utcClock).minus(1, ChronoUnit.DAYS),
      updatedAt = Instant.now(utcClock),
      deletedAt = Instant.now(utcClock)
    )

    whenever(notificationRepository.notification(notificationUuid)) doReturn notification

    // when
    connection.accept(UndoDeletedNotification(notificationUuid))

    // then
    verify(notificationRepository, times(1)).notification(notificationUuid)
    verify(notificationRepository, times(1)).undoNotificationDelete(notification)
    verifyNoMoreInteractions(notificationRepository)

    consumer.assertValues()
    viewActionsConsumer.assertValues()
  }
}
