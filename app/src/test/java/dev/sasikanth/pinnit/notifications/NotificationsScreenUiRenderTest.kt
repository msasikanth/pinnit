package dev.sasikanth.pinnit.notifications

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.sasikanth.pinnit.TestData
import org.junit.Test
import java.util.UUID

class NotificationsScreenUiRenderTest {

  private val ui = mock<NotificationsScreenUi>()
  private val uiRender = NotificationsScreenUiRender(ui)
  private val defaultModel = NotificationsScreenModel.default()

  @Test
  fun `when notifications are being fetched, then render nothing`() {
    // when
    uiRender.render(defaultModel)

    // then
    verifyZeroInteractions(ui)
  }

  @Test
  fun `show notifications and hide notification error if notifications are not empty`() {
    // given
    val notifications = listOf(
      TestData.notification(
        uuid = UUID.fromString("29238d40-c5c8-44e6-94f3-9b40fd0314b3")
      )
    )

    // when
    uiRender.render(defaultModel.onNotificationsLoaded(notifications))

    // then
    verify(ui).hideNotificationsEmptyError()
    verify(ui).showNotifications(notifications)
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `show notifications error and hide notifications if notifications are empty`() {
    // when
    uiRender.render(defaultModel.onNotificationsLoaded(emptyList()))

    // then
    verify(ui).showNotificationsEmptyError()
    verify(ui).hideNotifications()
    verifyNoMoreInteractions(ui)
  }
}
