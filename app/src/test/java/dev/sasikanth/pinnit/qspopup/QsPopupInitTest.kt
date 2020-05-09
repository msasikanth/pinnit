package dev.sasikanth.pinnit.qspopup

import com.spotify.mobius.test.FirstMatchers
import com.spotify.mobius.test.InitSpec
import org.junit.Test

class QsPopupInitTest {

  @Test
  fun `when screen is created, then load notifications`() {
    val initSpec = InitSpec(QsPopupInit())
    val defaultModel = QsPopupModel.default()

    initSpec
      .whenInit(defaultModel)
      .then(
        InitSpec.assertThatFirst(
          FirstMatchers.hasModel(defaultModel),
          FirstMatchers.hasEffects(LoadNotifications as QsPopupEffect)
        )
      )
  }
}
