package dev.sasikanth.pinnit.data.source.local

import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PinnitLocalDataSource(
    private val pinnitDao: PinnitDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

  fun notifications(): Flow<List<PinnitItem>> {
    return pinnitDao.notifications()
  }

  fun pinnedNotifications(): Flow<List<PinnitItem>> {
    return pinnitDao.pinnedNotifications()
  }

  suspend fun notification(key: Long): Result<PinnitItem> = withContext(ioDispatcher) {
    return@withContext try {
      val pinnitItem = pinnitDao.notification(key)
      if (pinnitItem != null) {
        Result.Success(pinnitItem)
      } else {
        Result.Error(NullPointerException("Notification is not found"))
      }
    } catch (e: Exception) {
      Result.Error(e)
    }
  }

  suspend fun insert(pinnitItem: PinnitItem) = withContext(ioDispatcher) {
    pinnitDao.insert(pinnitItem)
  }

  suspend fun pinStatus(id: Long, pinStatus: Boolean) = withContext(ioDispatcher) {
    pinnitDao.pinStatus(id, pinStatus)
  }

  suspend fun delete(key: Long) = withContext(ioDispatcher) {
    return@withContext pinnitDao.delete(key)
  }

  suspend fun deleteUnPinned() = withContext(ioDispatcher) {
    pinnitDao.deleteUnPinned()
  }
}
