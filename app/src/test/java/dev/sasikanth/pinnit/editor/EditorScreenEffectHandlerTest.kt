package dev.sasikanth.pinnit.editor

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.spotify.mobius.Connection
import com.spotify.mobius.test.RecordingConsumer
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.data.ScheduleType
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.TestDispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class EditorScreenEffectHandlerTest {

  private val testScope = TestCoroutineScope()

  private val viewEffectConsumer = RecordingConsumer<EditorScreenViewEffect>()
  private val repository = mock<NotificationRepository>()
  private val dispatcherProvider = TestDispatcherProvider()
  private val notificationUtil = mock<NotificationUtil>()
  private val effectHandler = EditorScreenEffectHandler(
    repository,
    dispatcherProvider,
    notificationUtil,
    viewEffectConsumer
  )

  private val consumer = RecordingConsumer<EditorScreenEvent>()
  private lateinit var connection: Connection<EditorScreenEffect>

  @Before
  fun setup() {
    connection = effectHandler.connect(consumer)
  }

  @After
  fun teardown() {
    connection.dispose()
  }

  @Test
  fun `when load notification effect is received, then load the notification`() = testScope.runBlockingTest {
    // given
    val notificationUuid = UUID.fromString("b44624c8-0535-4743-a97b-d0350fd446c2")
    val notification = TestData.notification(
      uuid = UUID.fromString("999f6f57-8ddd-41c2-886d-78d2e1c9b0b8")
    )

    whenever(repository.notification(notificationUuid)) doReturn notification

    // when
    connection.accept(LoadNotification(notificationUuid))

    // then
    verify(repository).notification(notificationUuid)
    verifyNoMoreInteractions(repository)

    consumer.assertValues(NotificationLoaded(notification))
  }

  @Test
  fun `when save notification effect is received, then save the notification`() = testScope.runBlockingTest {
    // given
    val notificationUuid = UUID.fromString("9610e5b7-6894-4da9-965a-048abf568247")
    val title = "Notification Title"
    val content = "This is content"
    val schedule = TestData.schedule(
      scheduleDate = LocalDate.parse("2020-01-01"),
      scheduleTime = LocalTime.parse("09:00:00"),
      scheduleType = ScheduleType.Daily
    )

    val notification = TestData.notification(
      uuid = notificationUuid,
      title = title,
      content = content
    )

    whenever(repository.save(eq(title), eq(content), eq(true), any())) doReturn notification

    // when
    connection.accept(SaveNotification(title, content, schedule))

    // then
    verify(repository).save(
      title = eq(title),
      content = eq(content),
      isPinned = eq(true),
      uuid = any()
    )
    verifyNoMoreInteractions(repository)

    consumer.assertValues(NotificationSaved(notification))
    viewEffectConsumer.assertValues()
  }

  @Test
  fun `when update notification effect is received, then update the notification`() = testScope.runBlockingTest {
    // given
    val notificationUuid = UUID.fromString("4e91382a-d5c3-44a7-8ee3-fa15a4ec69b4")
    val notification = TestData.notification(
      uuid = notificationUuid,
      title = "Notification Title"
    )

    val updatedTitle = "Updated Title"
    val updatedNotification = notification
      .copy(title = updatedTitle)

    val schedule = TestData.schedule(
      scheduleDate = LocalDate.parse("2020-01-01"),
      scheduleTime = LocalTime.parse("09:00:00"),
      scheduleType = ScheduleType.Daily
    )

    whenever(repository.notification(notificationUuid)) doReturn notification
    whenever(repository.updateNotification(updatedNotification)) doReturn updatedNotification

    // when
    connection.accept(
      UpdateNotification(
        notificationUuid = notificationUuid,
        title = updatedTitle,
        content = null,
        schedule = schedule
      )
    )

    // then
    verify(repository).notification(notificationUuid)
    verify(repository).updateNotification(updatedNotification)
    verifyNoMoreInteractions(repository)

    consumer.assertValues(NotificationUpdated(updatedNotification))
    viewEffectConsumer.assertValues()
  }

  @Test
  fun `when close editor effect is received, then close the editor view`() {
    // when
    connection.accept(CloseEditor)

    // then
    verifyZeroInteractions(repository)
    verifyZeroInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(CloseEditorView)
  }

  @Test
  fun `when show confirm exit effect is received, then show confirm exit dialog`() {
    // when
    connection.accept(ShowConfirmExitEditor)

    // then
    verifyZeroInteractions(repository)
    verifyZeroInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(ShowConfirmExitEditorDialog)
  }

  @Test
  fun `when delete notification effect is received, then delete the notification`() = testScope.runBlockingTest {
    // give
    val notification = TestData.notification(
      uuid = UUID.fromString("0e51b71a-2bec-49eb-bbec-1e5d1b74e643")
    )

    // when
    connection.accept(DeleteNotification(notification))

    // then
    verify(repository).toggleNotificationPinStatus(notification)
    verify(repository).deleteNotification(notification)
    verifyNoMoreInteractions(repository)
    verify(notificationUtil).dismissNotification(notification)
    verifyNoMoreInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(CloseEditorView)
  }

  @Test
  fun `when show confirm delete effect is received, then display the confirm delete dialog`() {
    // when
    connection.accept(ShowConfirmDelete)

    // then
    verifyZeroInteractions(repository)
    verifyZeroInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(ShowConfirmDeleteDialog)
  }

  @Test
  fun `when set title and content effect is received, then set title and content`() {
    // given
    val notificationContent = "Notification Content"

    // when
    connection.accept(SetTitleAndContent(null, notificationContent))

    // then
    verifyZeroInteractions(repository)
    verifyZeroInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(SetTitle(null), SetContent(notificationContent))
  }

  @Test
  fun `when show date picker effect is received, then show date picker dialog`() {
    // given
    val date = LocalDate.parse("2020-01-01")

    // when
    connection.accept(ShowDatePicker(date))

    // then
    verifyZeroInteractions(repository)
    verifyZeroInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(ShowDatePickerDialog(date))
  }

  @Test
  fun `when show time picker effect is received, then show time picker dialog`() {
    // given
    val time = LocalTime.parse("09:00:00")

    // when
    connection.accept(ShowTimePicker(time))

    // then
    verifyZeroInteractions(repository)
    verifyZeroInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(ShowTimePickerDialog(time))
  }

  @Test
  fun `when show notification effect is received, then show android notification`() {
    // given
    val notification = TestData.notification(
      uuid = UUID.fromString("e3848c84-afe9-45a6-ba90-d7f0ad3de193")
    )

    // when
    connection.accept(ShowNotification(notification))

    // then
    consumer.assertValues()
    viewEffectConsumer.assertValues()

    verify(notificationUtil).showNotification(notification)
  }
}
