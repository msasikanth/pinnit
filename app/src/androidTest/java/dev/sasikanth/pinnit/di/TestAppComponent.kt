package dev.sasikanth.pinnit.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [TestAppModule::class])
interface TestAppComponent : AppComponent {
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): TestAppComponent
  }
}
