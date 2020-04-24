package dev.sasikanth.pinnit.notifications

import com.spotify.mobius.test.FirstMatchers.hasEffects
import com.spotify.mobius.test.FirstMatchers.hasModel
import com.spotify.mobius.test.FirstMatchers.hasNoEffects
import com.spotify.mobius.test.InitSpec
import com.spotify.mobius.test.InitSpec.assertThatFirst
import dev.sasikanth.pinnit.TestData
import org.junit.Test
import java.util.UUID

class NotificationsScreenInitTest {

  private val initSpec = InitSpec(NotificationsScreenInit())
  private val defaultModel = NotificationsScreenModel.default()

  @Test
  fun `when screen is created, then load notifications`() {
    initSpec
      .whenInit(defaultModel)
      .then(
        assertThatFirst(
          hasModel(defaultModel),
          hasEffects(LoadNotifications as NotificationsScreenEffect)
        )
      )
  }

  @Test
  fun `when screen is restored, then don't load notifications`() {
    val notifications = listOf(
      TestData.notification(
        uuid = UUID.fromString("c59f2db1-2cfc-476c-9fa2-1fac99bd7336")
      )
    )
    val restoredModel = defaultModel
      .onNotificationsLoaded(notifications)

    initSpec
      .whenInit(restoredModel)
      .then(
        assertThatFirst(
          hasModel(restoredModel),
          hasNoEffects()
        )
      )
  }
}
