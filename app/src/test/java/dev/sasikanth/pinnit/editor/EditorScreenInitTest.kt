package dev.sasikanth.pinnit.editor

import com.spotify.mobius.test.FirstMatchers.hasEffects
import com.spotify.mobius.test.FirstMatchers.hasModel
import com.spotify.mobius.test.FirstMatchers.hasNoEffects
import com.spotify.mobius.test.InitSpec
import com.spotify.mobius.test.InitSpec.assertThatFirst
import dev.sasikanth.sharedtestcode.TestData
import org.junit.Test
import java.util.UUID

class EditorScreenInitTest {

  private val initSpec = InitSpec(EditorScreenInit())
  private val notificationUuid = UUID.fromString("97f6ee65-b2c6-403f-97aa-ca45ebfa444b")

  @Test
  fun `when screen is created and notification uuid is present, then fetch notification`() {
    val defaultModel = EditorScreenModel.default(notificationUuid, null, null)

    initSpec
      .whenInit(defaultModel)
      .then(
        assertThatFirst(
          hasModel(defaultModel),
          hasEffects(LoadNotification(notificationUuid) as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when screen is created and notification uuid is not present and title and content is null, then set empty title and content`() {
    val defaultModel = EditorScreenModel.default(null, null, null)

    initSpec
      .whenInit(defaultModel)
      .then(
        assertThatFirst(
          hasModel(defaultModel),
          hasEffects(SetTitleAndContent(defaultModel.title, defaultModel.content) as EditorScreenEffect)
        )
      )
  }

  @Test
  fun `when screen is restored and notification is already loaded, then do nothing`() {
    val notification = dev.sasikanth.sharedtestcode.TestData.notification(
      uuid = notificationUuid,
      title = "Notification Title"
    )

    val defaultModel = EditorScreenModel.default(notificationUuid, null, null)
      .notificationLoaded(notification)
      .titleChanged(notification.title)

    initSpec
      .whenInit(defaultModel)
      .then(
        assertThatFirst(
          hasModel(defaultModel),
          hasNoEffects()
        )
      )
  }
}
