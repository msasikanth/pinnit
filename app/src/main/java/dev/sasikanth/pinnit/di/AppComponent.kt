package dev.sasikanth.pinnit.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dev.sasikanth.pinnit.notifications.NotificationsScreen
import javax.inject.Scope

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): AppComponent
  }

  fun inject(target: NotificationsScreen)
}

@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class AppScope
