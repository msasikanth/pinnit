package dev.sasikanth.notif.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.sasikanth.notif.data.Message

class MessageStyleConverter {

    @TypeConverter
    fun fromMessagesList(messages: List<Message>): String {
        return Gson().toJson(messages)
    }

    @TypeConverter
    fun toTemplateStyle(messages: String): List<Message> {
        val type = object : TypeToken<List<Message>>() {}.type
        return Gson().fromJson(messages, type)
    }
}
