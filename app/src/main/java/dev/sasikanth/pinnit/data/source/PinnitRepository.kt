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

  fun notifications(): Flow<List<PinnitItem>> {
    return pinnitDataSource
        .notifications()
        .map {
          return@map it.distinctBy { pinnitItem ->
            PinnitItemDistinct.createFrom(pinnitItem)
          }
        }
  }

  fun pinnedNotifications(): Flow<List<PinnitItem>> {
    return pinnitDataSource.pinnedNotifications()
  }

  suspend fun notification(key: Long): Result<PinnitItem> {
    return pinnitDataSource.notification(key)
  }

  suspend fun insert(pinnitItem: PinnitItem): Long {
    val lastItemByKey = pinnitDataSource.notification(pinnitItem.key)
    if (lastItemByKey is Result.Success) {
      val isItemMatch = pinnitItem.equalsLastItem(lastItemByKey.data)
      if (isItemMatch) {
        return 0
      }
    }
    return pinnitDataSource.insert(pinnitItem)
  }

  suspend fun pinStatus(id: Long, isPinned: Boolean) {
    pinnitDataSource.pinStatus(id, isPinned)
  }

  suspend fun delete(key: Long) = pinnitDataSource.delete(key)

  suspend fun deleteUnPinned() {
    pinnitDataSource.deleteUnPinned()
  }
}
