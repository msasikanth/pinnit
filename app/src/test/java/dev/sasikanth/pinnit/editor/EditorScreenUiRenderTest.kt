package dev.sasikanth.pinnit.editor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.sasikanth.pinnit.TestData
import org.junit.Test
import java.util.UUID

class EditorScreenUiRenderTest {

  private val ui = mock<EditorScreenUi>()
  private val uiRender = EditorScreenUiRender(ui)

  private val notificationUuid = UUID.fromString("62f36ab9-9a54-481a-9db7-c856766975ce")
  private val notification = TestData.notification(
    uuid = notificationUuid
  )

  @Test
  fun `when notification is being fetched, then do nothing`() {
    // given
    val model = EditorScreenModel.default(null)
      .titleChanged(null)

    // when
    uiRender.render(model)

    // then
    verifyZeroInteractions(ui)
  }

  @Test
  fun `when title is not blank, then enable save`() {
    val notificationTitle = "Notification Title"

    val model = EditorScreenModel.default(notification)
      .titleChanged(notificationTitle)

    // then
    uiRender.render(model)

    // then
    verify(ui).enableSave()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when title is blank, then disable save`() {
    val title = ""

    val model = EditorScreenModel.default(notification)
      .titleChanged(title)

    // then
    uiRender.render(model)

    // then
    verify(ui).disableSave()
    verifyNoMoreInteractions(ui)
  }
}
