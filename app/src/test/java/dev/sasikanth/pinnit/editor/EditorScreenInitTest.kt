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

  @Test
  fun `when screen is created and notification uuid is present, then fetch notification`() {
    val notificationUuid = UUID.fromString("21221df8-4e51-4c26-8258-199a93ec2c04")
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
}
