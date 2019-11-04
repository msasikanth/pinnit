package dev.sasikanth.pinnit.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.sasikanth.pinnit.MainActivity
import dev.sasikanth.pinnit.MainViewModel
import dev.sasikanth.pinnit.pages.apps.AppsFragment
import dev.sasikanth.pinnit.pages.apps.AppsViewModel
import dev.sasikanth.pinnit.pages.currentnotifs.CurrentFragment
import dev.sasikanth.pinnit.pages.historynotifs.HistoryFragment
import dev.sasikanth.pinnit.services.PinnitListenerService
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, PreferenceModule::class, DataModule::class])
interface PinnitAppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context): PinnitAppComponent
    }

    // dependencies?
    val mainViewModel: MainViewModel
    val appsViewModel: AppsViewModel

    // inject into
    fun inject(mainActivity: MainActivity)

    fun inject(currentFragment: CurrentFragment)
    fun inject(historyFragment: HistoryFragment)
    fun inject(appsFragment: AppsFragment)
    fun inject(pinnitListenerService: PinnitListenerService)
}
