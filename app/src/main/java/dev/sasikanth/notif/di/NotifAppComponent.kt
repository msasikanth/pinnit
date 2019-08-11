package dev.sasikanth.notif.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.sasikanth.notif.MainActivity
import dev.sasikanth.notif.MainViewModel
import dev.sasikanth.notif.pages.apps.AppsFragment
import dev.sasikanth.notif.pages.apps.AppsViewModel
import dev.sasikanth.notif.pages.currentnotifs.CurrentFragment
import dev.sasikanth.notif.pages.historynotifs.HistoryFragment
import dev.sasikanth.notif.services.NotifListenerService
import dev.sasikanth.notif.utils.NotifPreferences
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, PreferenceModule::class, DataModule::class])
interface NotifAppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context): NotifAppComponent
    }

    // dependencies?
    val notifPreferences: NotifPreferences
    val mainViewModel: MainViewModel
    val appsViewModel: AppsViewModel

    // inject into
    fun inject(mainActivity: MainActivity)

    fun inject(currentFragment: CurrentFragment)
    fun inject(historyFragment: HistoryFragment)
    fun inject(appsFragment: AppsFragment)
    fun inject(notifListenerService: NotifListenerService)
}
