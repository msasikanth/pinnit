package dev.sasikanth.pinnit.di

import android.content.Context
import androidx.room.Room
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
    fun providesRoomDatabase(context: Context) = Room.databaseBuilder(
        context,
        NotifDatabase::class.java,
        "Notifs.db"
    ).build()

    @Provides
    @JvmStatic
    @Singleton
    fun providesNotifLocalDataSource(notifDatabase: NotifDatabase) = NotifLocalDataSource(
        notifDao = notifDatabase.notifDao
    )
}
