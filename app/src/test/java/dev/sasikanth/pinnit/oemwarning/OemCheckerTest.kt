package dev.sasikanth.pinnit.oemwarning

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OemCheckerTest {

  @Test
  fun `should show warning for oem should work correctly`() {
    // given
    val onePlus = "oneplus"

    // when
    val shouldShowOemWarningDialog = shouldShowWarningForOEM(onePlus)

    // then
    assertThat(shouldShowOemWarningDialog).isTrue()
  }

  @Test
  fun `should show warning for oem should fail`() {
    // given
    val google = "google"

    // when
    val shouldShowOemWarningDialog = shouldShowWarningForOEM(google)

    // then
    assertThat(shouldShowOemWarningDialog).isFalse()
  }
}
