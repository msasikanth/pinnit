package dev.sasikanth.notif.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.sasikanth.notif.MainActivity
import dev.sasikanth.notif.MainViewModel
import dev.sasikanth.notif.pages.currentpage.CurrentFragment
import dev.sasikanth.notif.pages.historypage.HistoryFragment
import dev.sasikanth.notif.services.NotifListenerService
import dev.sasikanth.notif.utils.NotifPreferences
import javax.inject.Singleton

@Singleton
@Component(modules = [PreferenceModule::class, DataModule::class])
interface NotifAppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context): NotifAppComponent
    }

    // dependencies?
    val notifPreferences: NotifPreferences
    val mainViewModel: MainViewModel

    // inject into
    fun inject(mainActivity: MainActivity)
    fun inject(currentFragment: CurrentFragment)
    fun inject(historyFragment: HistoryFragment)
    fun inject(notifListenerService: NotifListenerService)
}
