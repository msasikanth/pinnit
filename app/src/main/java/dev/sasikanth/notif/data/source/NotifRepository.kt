package dev.sasikanth.notif.data.source

import androidx.lifecycle.LiveData
import dev.sasikanth.notif.data.NotifItem
import dev.sasikanth.notif.data.Result
import dev.sasikanth.notif.data.source.local.NotifLocalDataSource

class NotifRepository(
    private val notifDataSource: NotifLocalDataSource
) {

    fun getNotifs(): LiveData<List<NotifItem>> {
        return notifDataSource.getNotifs()
    }

    suspend fun getNotifsByPackageName(packageName: String): Result<List<NotifItem>> {
        return notifDataSource.getNotifsByPackageName(packageName)
    }

    suspend fun getNotif(id: Long): Result<NotifItem> {
        return notifDataSource.getNotif(id)
    }

    suspend fun saveNotif(notifItem: NotifItem) {
        notifDataSource.saveNotif(notifItem)
    }

    suspend fun updateNotif(notifItem: NotifItem) {
        notifDataSource.updateNotif(notifItem)
    }

    suspend fun pinNotif(id: Long) {
        notifDataSource.pinNotif(id)
    }

    suspend fun unPinNotif(id: Long) {
        notifDataSource.unPinNotif(id)
    }

    suspend fun deleteNotif(id: Long) {
        notifDataSource.deleteNotif(id)
    }

    suspend fun deleteAllNotifs() {
        notifDataSource.deleteAllNotifs()
    }

    suspend fun deleteUnPinnedNotifs() {
        notifDataSource.deleteUnPinnedNotifs()
    }
}
