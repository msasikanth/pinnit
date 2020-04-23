package dev.sasikanth.pinnit.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.Instant
import java.util.UUID

@Entity
data class PinnitNotification(
  @PrimaryKey val uuid: UUID,
  val title: String,
  val content: String? = null,
  val isPinned: Boolean = true,
  val createdAt: Instant,
  val updatedAt: Instant,
  val deletedAt: Instant? = null
) {

  @Dao
  interface RoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(notifications: List<PinnitNotification>)

    @Query("UPDATE PinnitNotification SET isPinned = :isPinned WHERE uuid = :uuid")
    suspend fun updatePinStatus(uuid: UUID, isPinned: Boolean)

    @Query(
      """
        SELECT *
        FROM PinnitNotification
        WHERE deletedAt IS NULL
        ORDER BY isPinned DESC, updatedAt DESC
    """
    )
    fun notifications(): Flow<List<PinnitNotification>>

    @Query("SELECT * FROM PinnitNotification WHERE uuid = :uuid LIMIT 1")
    suspend fun notification(uuid: UUID): PinnitNotification
  }
}
