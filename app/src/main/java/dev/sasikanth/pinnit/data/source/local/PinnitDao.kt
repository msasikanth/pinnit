package dev.sasikanth.pinnit.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.sasikanth.pinnit.data.PinnitItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PinnitDao {

    @Query("SELECT * FROM notifs ORDER BY posted_on DESC")
    fun getNotifications(): Flow<List<PinnitItem>>

    @Query("SELECT * FROM notifs WHERE is_pinned == 1 ORDER BY posted_on DESC")
    fun getPinnedNotifications(): Flow<List<PinnitItem>>

    @Query("SELECT * FROM notifs WHERE _id == :id ORDER BY posted_on DESC LIMIT 1")
    suspend fun getNotificationById(id: Long): PinnitItem?

    @Query("SELECT * FROM notifs WHERE notif_key == :key ORDER BY posted_on DESC LIMIT 1")
    suspend fun getNotificationByKey(key: String): PinnitItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifItem(pinnitItem: PinnitItem): Long

    @Update
    suspend fun updateNotifItem(pinnitItem: PinnitItem): Int

    @Query("UPDATE notifs SET is_pinned = :isPinned WHERE _id == :id")
    suspend fun updateNotifPinStatus(id: Long, isPinned: Boolean)

    @Query("DELETE FROM notifs WHERE _id == :id")
    suspend fun deleteNotifById(id: Long): Int

    @Query("DELETE FROM notifs")
    suspend fun deleteNotifs()

    @Query("DELETE FROM notifs WHERE is_pinned == 0")
    suspend fun deleteUnPinnedNotifs()
}
