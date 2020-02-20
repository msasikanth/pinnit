package dev.sasikanth.pinnit.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.sasikanth.pinnit.data.PinnitItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PinnitDao {

  @Query("SELECT * FROM pinnit ORDER BY posted_on DESC")
  fun notifications(): Flow<List<PinnitItem>>

  @Query("SELECT * FROM pinnit WHERE is_pinned == 1 ORDER BY posted_on DESC")
  fun pinnedNotifications(): Flow<List<PinnitItem>>

  @Query("SELECT * FROM pinnit WHERE `key` == :key ORDER BY posted_on DESC LIMIT 1")
  suspend fun notification(key: Long): PinnitItem?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(pinnitItem: PinnitItem): Long

  @Query("UPDATE pinnit SET is_pinned = :isPinned WHERE `key` == :key")
  suspend fun pinStatus(key: Long, isPinned: Boolean)

  @Query("DELETE FROM pinnit WHERE `key` == :key")
  suspend fun delete(key: Long): Int

  @Query("DELETE FROM pinnit WHERE is_pinned == 0")
  suspend fun deleteUnPinned()
}
