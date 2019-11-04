package dev.sasikanth.pinnit.data.source

import androidx.lifecycle.LiveData
import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.Result
import dev.sasikanth.pinnit.data.source.local.PinnitLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinnitRepository
@Inject constructor(
    private val pinnitDataSource: PinnitLocalDataSource
) {

    fun getNotifs(): LiveData<List<PinnitItem>> {
        return pinnitDataSource.getNotifs()
    }

    fun getPinnedNotifs(): LiveData<List<PinnitItem>> {
        return pinnitDataSource.getPinnedNotifs()
    }

    suspend fun getNotif(id: Long): Result<PinnitItem> {
        return pinnitDataSource.getNotif(id)
    }

    suspend fun getNotif(key: String): PinnitItem? {
        return pinnitDataSource.getNotif(key)
    }

    suspend fun saveNotif(pinnitItem: PinnitItem): Long {
        val lastItemByKey = pinnitDataSource.getNotif(pinnitItem.notifKey)
        if (lastItemByKey != null) {
            val isItemMatch = pinnitItem.equalsLastItem(lastItemByKey)
            if (isItemMatch) {
                return 0
            }
        }
        return pinnitDataSource.saveNotif(pinnitItem)
    }

    suspend fun updateNotif(pinnitItem: PinnitItem) {
        pinnitDataSource.updateNotif(pinnitItem)
    }

    suspend fun pinNotif(id: Long) {
        pinnitDataSource.pinNotif(id)
    }

    suspend fun unPinNotif(id: Long) {
        pinnitDataSource.unPinNotif(id)
    }

    suspend fun deleteNotif(id: Long) = pinnitDataSource.deleteNotif(id)

    suspend fun deleteAllNotifs() {
        pinnitDataSource.deleteAllNotifs()
    }

    suspend fun deleteUnPinnedNotifs() {
        pinnitDataSource.deleteUnPinnedNotifs()
    }
}
