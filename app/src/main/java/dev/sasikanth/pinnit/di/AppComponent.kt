package dev.sasikanth.pinnit.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dev.sasikanth.pinnit.editor.EditorScreen
import dev.sasikanth.pinnit.notifications.NotificationsScreen
import dev.sasikanth.pinnit.options.OptionsBottomSheet
import javax.inject.Scope

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): AppComponent
  }

  fun inject(target: NotificationsScreen)
  fun inject(target: EditorScreen)
  fun inject(target: OptionsBottomSheet)
}

@Scope
@Retention(AnnotationRetention.SOURCE)
annotation class AppScope
