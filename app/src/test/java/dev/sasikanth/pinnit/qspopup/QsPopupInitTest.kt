package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.test.FirstMatchers
import com.spotify.mobius.test.InitSpec
import dev.sasikanth.sharedtestcode.TestData
import org.junit.Test
import java.util.UUID

class QsPopupInitTest {

  private val initSpec = InitSpec(QsPopupInit())
  private val defaultModel = QsPopupModel.default()

  @Test
  fun `when screen is created, then load notifications`() {
    initSpec
      .whenInit(defaultModel)
      .then(
        InitSpec.assertThatFirst(
          FirstMatchers.hasModel(defaultModel),
          FirstMatchers.hasEffects(LoadNotifications as QsPopupEffect)
        )
      )
  }

  @Test
  fun `when screen is restored and notifications are already loaded, then do nothing`() {
    val notifications = listOf(
      dev.sasikanth.sharedtestcode.TestData.notification(
        uuid = UUID.fromString("c59f2db1-2cfc-476c-9fa2-1fac99bd7336")
      )
    )
    val restoredModel = defaultModel
      .onNotificationsLoaded(notifications)

    initSpec
      .whenInit(restoredModel)
      .then(
        InitSpec.assertThatFirst(
          FirstMatchers.hasModel(restoredModel),
          FirstMatchers.hasNoEffects()
        )
      )
  }
}
