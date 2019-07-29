package dev.sasikanth.notif.di

import android.preference.PreferenceManager
import androidx.room.Room
import dev.sasikanth.notif.MainViewModel
import dev.sasikanth.notif.data.source.NotifRepository
import dev.sasikanth.notif.data.source.local.NotifDatabase
import dev.sasikanth.notif.data.source.local.NotifLocalDataSource
import dev.sasikanth.notif.utils.NotifPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val notifModules = module {
    single {
        Room.databaseBuilder(androidContext(), NotifDatabase::class.java, "Notifs.db")
            .build()
    }

    single {
        NotifRepository(
            NotifLocalDataSource(get<NotifDatabase>().notifDao)
        )
    }

    single {
        PreferenceManager.getDefaultSharedPreferences(androidContext())
    }

    single {
        NotifPreferences(androidContext(), get())
    }

    viewModel {
        MainViewModel(get())
    }
}
