package dev.sasikanth.pinnit.editor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import org.junit.Test

class EditorScreenUiRenderTest {

  private val ui = mock<EditorScreenUi>()
  private val uiRender = EditorScreenUiRender(ui)

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
}
