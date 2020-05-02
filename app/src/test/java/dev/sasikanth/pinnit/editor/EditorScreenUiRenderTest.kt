package dev.sasikanth.pinnit.editor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import dev.sasikanth.pinnit.TestData
import org.junit.Test
import java.util.UUID

class EditorScreenUiRenderTest {

  private val ui = mock<EditorScreenUi>()
  private val uiRender = EditorScreenUiRender(ui)

  private val notificationUuid = UUID.fromString("62f36ab9-9a54-481a-9db7-c856766975ce")

  @Test
  fun `when notification is being fetched or no notification is present, then show save and pin action button text`() {
    // given
    val model = EditorScreenModel.default(null)
      .titleChanged(null)

    // when
    uiRender.render(model)

    // then
    verify(ui).renderSaveAndPinActionButtonText()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when notification is already pinned, then show save action button text and enable save`() {
    // given
    val notification = TestData.notification(
      uuid = notificationUuid,
      title = "Notification Title",
      isPinned = true
    )
    val model = EditorScreenModel.default(notificationUuid)
      .notificationLoaded(notification)
      .titleChanged(notification.title)

    // when
    uiRender.render(model)

    // then
    verify(ui).renderSaveActionButtonText()
    verify(ui).enableSave()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when title is not blank, then enable save`() {
    val notificationTitle = "Notification Title"

    val model = EditorScreenModel.default(notificationUuid)
      .titleChanged(notificationTitle)

    // then
    uiRender.render(model)

    // then
    verify(ui).enableSave()
    verify(ui).renderSaveAndPinActionButtonText()
    verifyNoMoreInteractions(ui)
  }

  @Test
  fun `when title is blank, then disable save`() {
    val title = ""

    val model = EditorScreenModel.default(notificationUuid)
      .titleChanged(title)

    // then
    uiRender.render(model)

    // then
    verify(ui).disableSave()
    verify(ui).renderSaveAndPinActionButtonText()
    verifyNoMoreInteractions(ui)
  }
}
