package dev.sasikanth.pinnit.notifications

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.spotify.mobius.Connection
import com.spotify.mobius.test.RecordingConsumer
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.utils.TestDispatcherProvider
import dev.sasikanth.pinnit.utils.TestUtcClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.UUID

@ExperimentalCoroutinesApi
class NotificationsScreenEffectHandlerTest {

  private val utcClock = TestUtcClock().apply {
    setDate(LocalDate.parse("2020-02-14"))
  }

  private val consumer = RecordingConsumer<NotificationsScreenEvent>()
  private lateinit var connection: Connection<NotificationsScreenEffect>

  private val notificationRepository = mock<NotificationRepository>()
  private val testDispatcherProvider = TestDispatcherProvider()
  private val effectHandler = NotificationsScreenEffectHandler(
    notificationRepository,
    testDispatcherProvider
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
    verifyZeroInteractions(uiActions)

    consumer.assertValues(NotificationsLoaded(notifications))
  }
}