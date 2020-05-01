package dev.sasikanth.pinnit.editor

import com.spotify.mobius.test.NextMatchers.hasEffects
import com.spotify.mobius.test.NextMatchers.hasModel
import com.spotify.mobius.test.NextMatchers.hasNoEffects
import com.spotify.mobius.test.NextMatchers.hasNoModel
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import dev.sasikanth.pinnit.TestData
import org.junit.Test
import java.util.UUID

class EditorScreenUpdateTest {

  private val updateSpec = UpdateSpec(EditorScreenUpdate())
  private val notificationUuid = UUID.fromString("87722f77-865a-4df3-8c82-9d1c7b3fd5bb")
  private val notification = TestData.notification(
    uuid = notificationUuid
  )
  private val defaultModel = EditorScreenModel.default(notification)

  @Test
  fun `when notification is loaded, then update the ui`() {
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

  @Test
  fun `when content changes, then update ui`() {
    val content = "Content"

    updateSpec
      .given(defaultModel)
      .whenEvent(ContentChanged(content))
      .then(
        assertThatNext(
          hasModel(defaultModel.contentChanged(content)),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when save is clicked and notification uuid is not present, then save the notification`() {
    val model = EditorScreenModel
      .default(null)
      .titleChanged("Title")
      .contentChanged("Content")

    updateSpec
      .given(model)
      .whenEvent(SaveClicked)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(SaveNotificationAndCloseEditor(model.title!!, model.content) as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when save is clicked and notification uuid is present, then update the notification`() {
    val model = defaultModel
      .titleChanged("Title")
      .contentChanged("Content")

    updateSpec
      .given(model)
      .whenEvent(SaveClicked)
      .then(
        assertThatNext(
          hasNoModel(),
          hasEffects(UpdateNotificationAndCloseEditor(notificationUuid, model.title!!, model.content) as EditorScreenEffect)
        )
      )
  }
}
