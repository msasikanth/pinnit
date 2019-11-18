package dev.sasikanth.pinnit.services

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ServiceTestRule
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeoutException
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class PinnitListenerServiceTest {

    @get:Rule
    val serviceRule = ServiceTestRule()

    @Test
    @Throws(TimeoutException::class)
    fun testWithBoundService() {
        // Create the service Intent.
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            PinnitListenerService::class.java
        )
        serviceRule.bindService(serviceIntent)

        // Get the reference to the service
        val service = PinnitListenerService.getInstance()

        // Verify that the service is working correctly.
        assertThat(service).isNotNull()
    }
}
