package dev.sasikanth.pinnit.data.source.local

import androidx.lifecycle.LiveData
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PinnitLocalDataSource(
    private val pinnitDao: PinnitDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getNotifs(): LiveData<List<PinnitItem>> {
        return pinnitDao.getNotifications()
    }

    fun getPinnedNotifs(): LiveData<List<PinnitItem>> {
        return pinnitDao.getPinnedNotifications()
    }

    suspend fun getNotif(id: Long): Result<PinnitItem> = withContext(ioDispatcher) {
        return@withContext try {
            val notifItem = pinnitDao.getNotificationById(id)
            if (notifItem != null) {
                Result.Success(notifItem)
            } else {
                Result.Error(NullPointerException("Notification is not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getNotif(key: String): PinnitItem? = withContext(ioDispatcher) {
        return@withContext try {
            pinnitDao.getNotificationByKey(key)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveNotif(pinnitItem: PinnitItem) = withContext(ioDispatcher) {
        pinnitDao.insertNotifItem(pinnitItem)
    }

    suspend fun updateNotif(pinnitItem: PinnitItem) = withContext(ioDispatcher) {
        pinnitDao.updateNotifItem(pinnitItem)
    }

    suspend fun pinNotif(id: Long) = withContext(ioDispatcher) {
        pinnitDao.updateNotifPinStatus(id, true)
    }

    suspend fun unPinNotif(id: Long) = withContext(ioDispatcher) {
        pinnitDao.updateNotifPinStatus(id, false)
    }

    suspend fun deleteNotif(id: Long) = withContext(ioDispatcher) {
        return@withContext pinnitDao.deleteNotifById(id)
    }

    suspend fun deleteAllNotifs() = withContext(ioDispatcher) {
        pinnitDao.deleteNotifs()
    }

    suspend fun deleteUnPinnedNotifs() = withContext(ioDispatcher) {
        pinnitDao.deleteUnPinnedNotifs()
    }
}
