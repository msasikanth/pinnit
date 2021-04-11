package dev.sasikanth.pinnit.data.preferences

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object AppPreferencesSerializer : Serializer<AppPreferences> {

  override val defaultValue: AppPreferences = AppPreferences.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): AppPreferences {
    try {
      return AppPreferences.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: AppPreferences, output: OutputStream) = t.writeTo(output)
}
