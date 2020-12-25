package dev.sasikanth.pinnit

import android.app.Application
import dev.sasikanth.pinnit.di.DaggerTestAppComponent
import dev.sasikanth.pinnit.di.TestAppComponent

class TestPinnitApp : Application() {

  val component: TestAppComponent by lazy {
    DaggerTestAppComponent
      .factory()
      .create(this)
  }
}
