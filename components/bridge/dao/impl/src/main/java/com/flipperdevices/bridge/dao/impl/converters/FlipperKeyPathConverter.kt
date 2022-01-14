package com.flipperdevices.bridge.dao.impl.converters

import androidx.room.TypeConverter
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import java.io.File

class FlipperKeyPathConverter {
    @TypeConverter
    fun pathToFlipperKeyPath(path: String?): FlipperKeyPath? {
        val pathNotNull = path ?: return null
        val file = File(pathNotNull)
        return FlipperKeyPath(file.parent ?: "", file.name)
    }

    @TypeConverter
    fun flipperKeyPathToPath(keyPath: FlipperKeyPath?): String? {
        return keyPath?.pathToKey
    }
}
