package dev.sasikanth.notif.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notif_item")
data class NotifItem(
    @PrimaryKey(autoGenerate = true)
    val _id: Long,
    @ColumnInfo(name = "notif_key")
    val notifKey: String,
    @ColumnInfo(name = "notif_id")
    val notifId: Int,
    @ColumnInfo(name = "large_icon")
    val iconBytes: ByteArray?,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "posted_on")
    val postedOn: Long,
    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotifItem

        if (_id != other._id) return false
        if (notifKey != other.notifKey) return false
        if (notifId != other.notifId) return false
        if (iconBytes != null) {
            if (other.iconBytes == null) return false
            if (!iconBytes.contentEquals(other.iconBytes)) return false
        } else if (other.iconBytes != null) return false
        if (title != other.title) return false
        if (text != other.text) return false
        if (packageName != other.packageName) return false
        if (postedOn != other.postedOn) return false
        if (isPinned != other.isPinned) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + notifKey.hashCode()
        result = 31 * result + notifId
        result = 31 * result + (iconBytes?.contentHashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + packageName.hashCode()
        result = 31 * result + postedOn.hashCode()
        result = 31 * result + isPinned.hashCode()
        return result
    }
}
