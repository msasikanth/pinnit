package dev.sasikanth.pinnit.editor

import com.spotify.mobius.test.FirstMatchers.hasEffects
import com.spotify.mobius.test.FirstMatchers.hasModel
import com.spotify.mobius.test.InitSpec
import com.spotify.mobius.test.InitSpec.assertThatFirst
import org.junit.Test
import java.util.UUID

class EditorScreenInitTest {

  @Test
  fun `when screen is created and notification uuid is present, then fetch notification`() {
    val initSpec = InitSpec(EditorScreenInit())
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
}
