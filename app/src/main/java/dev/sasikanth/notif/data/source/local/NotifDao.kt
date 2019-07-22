package dev.sasikanth.notif.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.sasikanth.notif.data.NotifItem

@Dao
interface NotifDao {

    @Query("SELECT * FROM notifs ORDER BY posted_on DESC")
    fun getNotifications(): LiveData<List<NotifItem>>

    @Query("SELECT * FROM notifs WHERE _id == :id ORDER BY posted_on DESC LIMIT 1")
    suspend fun getNotificationById(id: Long): NotifItem?

    @Query("SELECT * FROM notifs WHERE package_name == :packageName ORDER BY posted_on DESC")
    suspend fun getNotificationsByPackageName(packageName: String): List<NotifItem>

    @Query("SELECT * FROM notifs WHERE notif_key == :key ORDER BY posted_on DESC LIMIT 1")
    suspend fun getNotificationByKey(key: String): NotifItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifItem(notifItem: NotifItem): Long

    @Update
    suspend fun updateNotifItem(notifItem: NotifItem): Int

    @Query("UPDATE notifs SET is_pinned = :isPinned WHERE _id == :id")
    suspend fun updateNotifPinStatus(id: Long, isPinned: Boolean)

    @Query("DELETE FROM notifs WHERE _id == :id")
    suspend fun deleteNotifById(id: Long): Int

    @Query("DELETE FROM notifs")
    suspend fun deleteNotifs()

    @Query("DELETE FROM notifs WHERE is_pinned == 0")
    suspend fun deleteUnPinnedNotifs()
}
