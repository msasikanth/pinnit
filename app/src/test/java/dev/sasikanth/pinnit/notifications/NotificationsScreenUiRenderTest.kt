package dev.sasikanth.pinnit.notifications

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.Test

class NotificationsScreenUiRenderTest {

  @Test
  fun `when notifications are being fetched, then render nothing`() {
    // given
    val ui = mock<NotificationsScreenUi>()
    val uiRender = NotificationsScreenUiRender(ui)
    val defaultModel = NotificationsScreenModel.default()

    // when
    uiRender.render(defaultModel)

    // then
    verifyZeroInteractions(ui)
  }
}
