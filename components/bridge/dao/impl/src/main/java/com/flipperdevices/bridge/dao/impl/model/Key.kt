package com.flipperdevices.bridge.dao.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

@Entity(
    tableName = "keys",
    indices = [
        Index(value = ["path"], unique = true), // We can't storage two keys with one path
        Index(value = ["type"]) // For performance
    ]
)
data class Key(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "path") val path: FlipperKeyPath,
    @ColumnInfo(name = "type") val type: FlipperFileType? = path.fileType, // Denormalize for performance
    @ColumnInfo(name = "file_path") val filePath: String
)
