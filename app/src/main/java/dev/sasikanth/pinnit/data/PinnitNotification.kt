package dev.sasikanth.pinnit.data

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.UUID

@Keep
@Entity
@Parcelize
data class PinnitNotification(
  @PrimaryKey val uuid: UUID,
  val title: String,
  val content: String? = null,
  val isPinned: Boolean = true,
  val createdAt: Instant,
  val updatedAt: Instant,
  val deletedAt: Instant? = null,
  @Embedded
  val schedule: Schedule? = null
) : Parcelable {

  fun equalsTitleAndContent(title: String?, content: String?) =
    this.title == title.orEmpty() && this.content.orEmpty() == content.orEmpty()

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

    @Query(
      """
        SELECT *
        FROM PinnitNotification
        WHERE deletedAt IS NULL AND isPinned = 1
        ORDER BY updatedAt DESC
      """
    )
    suspend fun pinnedNotifications(): List<PinnitNotification>

    @Query("SELECT * FROM PinnitNotification WHERE uuid = :uuid LIMIT 1")
    suspend fun notification(uuid: UUID): PinnitNotification
  }
}
