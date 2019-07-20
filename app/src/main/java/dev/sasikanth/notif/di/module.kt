package dev.sasikanth.notif.di

import androidx.room.Room
import dev.sasikanth.notif.data.source.NotifRepository
import dev.sasikanth.notif.data.source.local.NotifDatabase
import dev.sasikanth.notif.data.source.local.NotifLocalDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notifModules = module {
    single {
        Room.databaseBuilder(androidContext(), NotifDatabase::class.java, "Notifs.db")
            .build()
    }

    single {
        NotifRepository(
            NotifLocalDataSource(get<NotifDatabase>().notifDao())
        )
    }
}
