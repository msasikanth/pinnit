package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.test.FirstMatchers.hasEffects
import com.spotify.mobius.test.FirstMatchers.hasModel
import com.spotify.mobius.test.InitSpec
import com.spotify.mobius.test.InitSpec.assertThatFirst
import org.junit.Test

class NotificationsScreenInitTest {

  @Test
  fun `when screen is created, then load notifications`() {
    val initSpec = InitSpec(NotificationsScreenInit())
    val defaultModel = NotificationsScreenModel.default()

    initSpec
      .whenInit(defaultModel)
      .then(
        assertThatFirst(
          hasModel(defaultModel),
          hasEffects(LoadNotifications as NotificationsScreenEffect)
        )
      )
  }
}
