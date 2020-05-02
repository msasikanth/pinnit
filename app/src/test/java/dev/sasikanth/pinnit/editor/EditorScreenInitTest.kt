package dev.sasikanth.pinnit.editor

import com.spotify.mobius.test.FirstMatchers.hasEffects
import com.spotify.mobius.test.FirstMatchers.hasModel
import com.spotify.mobius.test.FirstMatchers.hasNoEffects
import com.spotify.mobius.test.InitSpec
import com.spotify.mobius.test.InitSpec.assertThatFirst
import org.junit.Test
import java.util.UUID

class EditorScreenInitTest {

  private val initSpec = InitSpec(EditorScreenInit())
  private val notificationUuid = UUID.fromString("97f6ee65-b2c6-403f-97aa-ca45ebfa444b")

  @Test
  fun `when screen is created and notification uuid is present, then fetch notification`() {
    val defaultModel = EditorScreenModel.default(notificationUuid)

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
  fun `when screen is created and notification uuid is not present, then do nothing`() {
    val defaultModel = EditorScreenModel.default(null)

    initSpec
      .whenInit(defaultModel)
      .then(
        assertThatFirst(
          hasModel(defaultModel),
          hasNoEffects()
        )
      )
  }

  @Test
  fun `when screen is restored and notification is already loaded, then do nothing`() {
    val defaultModel = EditorScreenModel.default(notificationUuid)
      .titleChanged("Notification Title")

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
