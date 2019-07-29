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

    fun getPinnedNotifs(): LiveData<List<NotifItem>> {
        return notifDataSource.getPinnedNotifs()
    }

    suspend fun getNotif(id: Long): Result<NotifItem> {
        return notifDataSource.getNotif(id)
    }

    suspend fun saveNotif(notifItem: NotifItem) {
        val lastItemByKey = notifDataSource.getNotif(notifItem.notifKey)
        if (lastItemByKey != null) {
            val isItemMatch = notifItem.equalsLastItem(lastItemByKey)
            if (isItemMatch) {
                return
            }
        }
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
