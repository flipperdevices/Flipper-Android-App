package com.flipperdevices.bridge.dao.impl.converters

import androidx.room.TypeConverter
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType

class FlipperFileTypeConverter {
    @TypeConverter
    fun extensionToFileType(extension: String?): FlipperKeyType? {
        val extensionNotNull = extension ?: return null
        return FlipperKeyType.getByExtension(extensionNotNull)
    }

    @TypeConverter
    fun fileTypeToExtension(fileType: FlipperKeyType?): String? {
        return fileType?.extension
    }
}
