package dev.sasikanth.pinnit

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CanaryAndroidTest {

  @Test
  fun testEnvWorks() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    assertThat(context).isNotNull()
  }
}
