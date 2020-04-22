package dev.sasikanth.pinnit

import android.app.Application
import dev.sasikanth.pinnit.di.ComponentProvider
import dev.sasikanth.pinnit.di.DaggerTestAppComponent
import dev.sasikanth.pinnit.di.TestAppComponent

class TestPinnitApp : Application(), ComponentProvider {
  override val component: TestAppComponent by lazy {
    DaggerTestAppComponent
      .factory()
      .create(this)
  }
}
