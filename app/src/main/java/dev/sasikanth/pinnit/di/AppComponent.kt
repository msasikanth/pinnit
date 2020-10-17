package dev.sasikanth.pinnit.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dev.sasikanth.pinnit.PinnitApp
import dev.sasikanth.pinnit.activity.MainActivity
import dev.sasikanth.pinnit.background.receivers.AppUpdateReceiver
import dev.sasikanth.pinnit.background.receivers.BootCompletedReceiver
import dev.sasikanth.pinnit.background.receivers.DeleteNotificationReceiver
import dev.sasikanth.pinnit.background.receivers.UnpinNotificationReceiver
import dev.sasikanth.pinnit.editor.EditorScreen
import dev.sasikanth.pinnit.notifications.NotificationsScreen
import dev.sasikanth.pinnit.options.OptionsBottomSheet
import dev.sasikanth.pinnit.qspopup.QsPopupActivity
import dev.sasikanth.pinnit.worker.ScheduleWorker
import javax.inject.Scope

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent {

  @Component.Factory
  interface Factory {
    fun create(@BindsInstance application: Application): AppComponent
  }

  fun inject(target: PinnitApp)

  fun inject(target: MainActivity)
  fun inject(target: QsPopupActivity)
  fun inject(target: NotificationsScreen)
  fun inject(target: EditorScreen)

  fun inject(target: OptionsBottomSheet)
  fun inject(target: UnpinNotificationReceiver)
  fun inject(target: BootCompletedReceiver)
  fun inject(target: AppUpdateReceiver)
  fun inject(target: DeleteNotificationReceiver)

  fun inject(target: ScheduleWorker)
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope
