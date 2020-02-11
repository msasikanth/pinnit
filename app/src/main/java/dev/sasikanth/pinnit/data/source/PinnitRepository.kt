package dev.sasikanth.pinnit.data.source

import dev.sasikanth.pinnit.data.PinnitItem
import dev.sasikanth.pinnit.data.PinnitItemDistinct
import dev.sasikanth.pinnit.data.Result
import dev.sasikanth.pinnit.data.source.local.PinnitLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinnitRepository
@Inject constructor(
    private val pinnitDataSource: PinnitLocalDataSource
) {

  fun getNotifs(): Flow<List<PinnitItem>> {
    return pinnitDataSource
        .getNotifs()
        .map {
          return@map it.distinctBy { pinnitItem ->
            PinnitItemDistinct.createFrom(pinnitItem)
          }
        }
  }

  fun getPinnedNotifs(): Flow<List<PinnitItem>> {
    return pinnitDataSource.getPinnedNotifs()
  }

  suspend fun getNotif(key: Long): Result<PinnitItem> {
    return pinnitDataSource.getNotif(key)
  }

  suspend fun saveNotif(pinnitItem: PinnitItem): Long {
    val lastItemByKey = pinnitDataSource.getNotif(pinnitItem.notifKey)
    if (lastItemByKey is Result.Success) {
      val isItemMatch = pinnitItem.equalsLastItem(lastItemByKey.data)
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
