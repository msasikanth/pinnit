package dev.sasikanth.pinnit.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dev.sasikanth.pinnit.notifications.NotificationsRepositoryAndroidTest

@AppScope
@Component(modules = [TestAppModule::class])
interface TestAppComponent {
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): TestAppComponent
  }

  fun inject(target: NotificationsRepositoryAndroidTest)
}
