package dev.sasikanth.pinnit.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dev.sasikanth.pinnit.data.source.local.NotifDatabase
import dev.sasikanth.pinnit.data.source.local.NotifLocalDataSource
import javax.inject.Singleton

@Module
object DataModule {

    @Provides
    @JvmStatic
    @Singleton
    fun providesRoomDatabase(context: Context) = NotifDatabase.createDatabase(context)

    @Provides
    @JvmStatic
    @Singleton
    fun providesNotifLocalDataSource(notifDatabase: NotifDatabase) = NotifLocalDataSource(
        notifDao = notifDatabase.notifDao
    )
}
