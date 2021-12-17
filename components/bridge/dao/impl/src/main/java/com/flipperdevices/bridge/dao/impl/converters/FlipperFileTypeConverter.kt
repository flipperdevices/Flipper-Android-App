package com.flipperdevices.bridge.dao.impl.converters

import androidx.room.TypeConverter
import com.flipperdevices.bridge.dao.api.model.FlipperFileType

class FlipperFileTypeConverter {
    @TypeConverter
    fun extensionToFileType(extension: String?): FlipperFileType? {
        val extensionNotNull = extension ?: return null
        return FlipperFileType.getByExtension(extensionNotNull)
    }

    @TypeConverter
    fun fileTypeToExtension(fileType: FlipperFileType?): String? {
        return fileType?.extension
    }
}
