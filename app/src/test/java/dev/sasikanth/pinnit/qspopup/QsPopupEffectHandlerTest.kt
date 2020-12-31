package dev.sasikanth.pinnit.qspopup

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
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.scheduler.PinnitNotificationScheduler
import dev.sasikanth.pinnit.utils.TestDispatcherProvider
import dev.sasikanth.pinnit.utils.TestUtcClock
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class QsPopupEffectHandlerTest {

  private val testScope = TestCoroutineScope()
  private val utcClock = TestUtcClock()
  private val consumer = RecordingConsumer<QsPopupEvent>()
  private val viewEffectConsumer = RecordingConsumer<QsPopupViewEffect>()

  private val notificationRepository = mock<NotificationRepository>()
  private val notificationUtil = mock<NotificationUtil>()
  private val pinnitNotificationScheduler = mock<PinnitNotificationScheduler>()

  private val effectHandler = QsPopupEffectHandler(
    dispatcherProvider = TestDispatcherProvider(),
    notificationRepository = notificationRepository,
    notificationUtil = notificationUtil,
    pinnitNotificationScheduler = pinnitNotificationScheduler,
    viewEffectConsumer = viewEffectConsumer
  )

  private lateinit var connection: Connection<QsPopupEffect>

  @Before
  fun setup() {
    connection = effectHandler.connect(consumer)
  }

  @After
  fun tearDown() {
    connection.dispose()
  }

  @Test
  fun `when load notifications effect is received, then load notifications`() {
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
  }

  @Test
  fun `when open edit screen effect is received, then open the edit page`() {
    // given
    val notificationUuid = UUID.fromString("220c2037-94ba-44ef-8f83-5c232f01288f")
    val notification = TestData.notification(
      uuid = notificationUuid,
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )

    // when
    connection.accept(OpenNotificationEditor(notification))

    // then
    verifyZeroInteractions(notificationRepository)

    consumer.assertValues()
    viewEffectConsumer.assertValues(OpenNotificationEditorViewEffect(notification))
  }

  @Test
  fun `when toggle pin status effect is received, then update the notification pin status`() = testScope.runBlockingTest {
    // given
    val notification = TestData.notification(
      uuid = UUID.fromString("ff73fd70-852f-4833-bc9c-a6f67b2e66f0"),
      isPinned = true,
      createdAt = Instant.now(utcClock),
      updatedAt = Instant.now(utcClock)
    )

    // when
    connection.accept(ToggleNotificationPinStatus(notification))

    // then
    verify(notificationRepository, times(1)).updatePinStatus(notification.uuid, false)
    verifyNoMoreInteractions(notificationRepository)

    verify(notificationUtil).dismissNotification(notification)
    verifyNoMoreInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues()
  }

  @Test
  fun `when cancel notification schedule effect is received, then cancel the notification schedule`() = testScope.runBlockingTest {
    // given
    val notification = TestData.notification(
      schedule = TestData.schedule()
    )

    // when
    connection.accept(CancelNotificationSchedule(notification.uuid))

    // then
    verify(pinnitNotificationScheduler).cancel(notification.uuid)
    verifyNoMoreInteractions(pinnitNotificationScheduler)

    consumer.assertValues()
    viewEffectConsumer.assertValues()
  }
}
