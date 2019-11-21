package dev.sasikanth.pinnit.data.converters

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class UriTypeConverter {

    @TypeConverter
    fun fromUri(uri: Uri?) = uri?.toString().orEmpty()

    @TypeConverter
    fun toUri(uri: String) = uri.toUri()
}
