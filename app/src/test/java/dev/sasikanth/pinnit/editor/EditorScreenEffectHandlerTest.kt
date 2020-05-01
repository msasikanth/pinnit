package dev.sasikanth.pinnit.editor

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.spotify.mobius.Connection
import com.spotify.mobius.test.RecordingConsumer
import dev.sasikanth.pinnit.TestData
import dev.sasikanth.pinnit.notifications.NotificationRepository
import dev.sasikanth.pinnit.utils.TestDispatcherProvider
import dev.sasikanth.pinnit.utils.notification.NotificationUtil
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID

class EditorScreenEffectHandlerTest {

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
  fun `when load notification effect is received, then load the notification`() = runBlocking {
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
    viewEffectConsumer.assertValues(SetTitle(notification.title), SetContent(notification.content))
  }

  @Test
  fun `when save and close effect is received, then save the notification and close editor`() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("9610e5b7-6894-4da9-965a-048abf568247")
    val title = "Notification Title"
    val content = "This is content"

    val notification = TestData.notification(
      uuid = notificationUuid,
      title = title,
      content = content
    )

    whenever(repository.save(eq(title), eq(content), eq(true), any())) doReturn notification

    // when
    connection.accept(SaveNotificationAndCloseEditor(title, content))

    // then
    verify(repository).save(
      title = eq(title),
      content = eq(content),
      isPinned = eq(true),
      uuid = any()
    )
    verifyNoMoreInteractions(repository)

    verify(notificationUtil).showNotification(notification)
    verifyNoMoreInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(CloseEditor)
  }

  @Test
  fun `when update and close effect is received, then update the notification and close editor`() = runBlocking {
    // given
    val notificationUuid = UUID.fromString("4e91382a-d5c3-44a7-8ee3-fa15a4ec69b4")
    val notification = TestData.notification(
      uuid = notificationUuid,
      title = "Notification Title"
    )

    val updatedTitle = "Updated Title"
    val updatedNotification = notification
      .copy(
        title = updatedTitle
      )

    whenever(repository.notification(notificationUuid)) doReturn notification
    whenever(repository.updateNotification(updatedNotification)) doReturn updatedNotification

    // when
    connection.accept(UpdateNotificationAndCloseEditor(notificationUuid, updatedTitle, null))

    // then
    verify(repository).notification(notificationUuid)
    verify(repository).updateNotification(updatedNotification)
    verifyNoMoreInteractions(repository)

    verify(notificationUtil).showNotification(updatedNotification)
    verifyNoMoreInteractions(notificationUtil)

    consumer.assertValues()
    viewEffectConsumer.assertValues(CloseEditor)
  }
}
