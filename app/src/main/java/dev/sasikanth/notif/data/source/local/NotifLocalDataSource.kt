package dev.sasikanth.notif.data.source.local

import androidx.lifecycle.LiveData
import dev.sasikanth.notif.data.NotifItem
import dev.sasikanth.notif.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotifLocalDataSource(
    private val notifDao: NotifDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getNotifs(): LiveData<List<NotifItem>> {
        return notifDao.getNotifications()
    }

    fun getPinnedNotifs(): LiveData<List<NotifItem>> {
        return notifDao.getPinnedNotifications()
    }

    suspend fun getNotif(id: Long): Result<NotifItem> = withContext(ioDispatcher) {
        return@withContext try {
            val notifItem = notifDao.getNotificationById(id)
            if (notifItem != null) {
                Result.Success(notifItem)
            } else {
                Result.Error(NullPointerException("Notification is not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun saveNotif(notifItem: NotifItem) = withContext(ioDispatcher) {
        notifDao.insertNotifItem(notifItem)
    }

    suspend fun updateNotif(notifItem: NotifItem) = withContext(ioDispatcher) {
        notifDao.updateNotifItem(notifItem)
    }

    suspend fun pinNotif(id: Long) = withContext(ioDispatcher) {
        notifDao.updateNotifPinStatus(id, true)
    }

    suspend fun unPinNotif(id: Long) = withContext(ioDispatcher) {
        notifDao.updateNotifPinStatus(id, false)
    }

    suspend fun deleteNotif(id: Long) = withContext(ioDispatcher) {
        notifDao.deleteNotifById(id)
    }

    suspend fun deleteAllNotifs() = withContext(ioDispatcher) {
        notifDao.deleteNotifs()
    }

    suspend fun deleteUnPinnedNotifs() = withContext(ioDispatcher) {
        notifDao.deleteUnPinnedNotifs()
    }
}
