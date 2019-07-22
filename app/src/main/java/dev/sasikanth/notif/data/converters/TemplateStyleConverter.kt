package dev.sasikanth.notif.data.converters

import androidx.room.TypeConverter
import dev.sasikanth.notif.data.TemplateStyle

class TemplateStyleConverter {

    @TypeConverter
    fun fromTemplateStyle(templateStyle: TemplateStyle): String {
        return templateStyle.name
    }

    @TypeConverter
    fun toTemplateStyle(templateStyle: String): TemplateStyle {
        return TemplateStyle.valueOf(templateStyle)
    }
}
