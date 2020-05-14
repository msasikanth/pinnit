package dev.sasikanth.pinnit.qspopup

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.spotify.mobius.Connection
import com.spotify.mobius.test.RecordingConsumer
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.TestDispatcherProvider
import dev.sasikanth.pinnit.utils.TestUtcClock
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit
import java.util.UUID

class QsPopupEffectHandlerTest {

  private val utcClock = TestUtcClock()
  private val consumer = RecordingConsumer<QsPopupEvent>()
  private val viewEffectConsumer = RecordingConsumer<QsPopupViewEffect>()

  private val notificationRepository = mock<NotificationRepository>()

  private val effectHandler = QsPopupEffectHandler(
    dispatcherProvider = TestDispatcherProvider(),
    notificationRepository = notificationRepository,
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
}
