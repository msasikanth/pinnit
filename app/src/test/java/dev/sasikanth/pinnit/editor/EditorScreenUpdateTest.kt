package dev.sasikanth.pinnit.editor

import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import dev.sasikanth.pinnit.TestData
import org.junit.Test
import java.util.UUID

class EditorScreenUpdateTest {

  private val updateSpec = UpdateSpec(EditorScreenUpdate())
  private val notificationUuid = UUID.fromString("87722f77-865a-4df3-8c82-9d1c7b3fd5bb")
  private val defaultModel = EditorScreenModel.default(notificationUuid)

  @Test
  fun `when notification is loaded, then update the ui`() {
    val notification = TestData.notification(
      uuid = notificationUuid
    )

    updateSpec
      .given(defaultModel)
      .whenEvent(NotificationLoaded(notification))
      .then(
        assertThatNext(
          hasModel(
            defaultModel
              .titleChanged(notification.title)
              .contentChanged(notification.content)
          ),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when title changes and is not empty, then update ui`() {
    val title = "Title"

    updateSpec
      .given(defaultModel)
      .whenEvent(TitleChanged(title))
      .then(
        assertThatNext(
          hasModel(defaultModel.titleChanged(title)),
          hasNoEffects()
        )
      )
  }
}
