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
        Index(
            value = ["path", "deleted"],
            unique = true
        ), // We can't storage two keys with one path
        Index(value = ["type"]) // For performance
    ]
)
data class Key(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "path") val path: FlipperKeyPath,
    // Denormalize for performance
    @ColumnInfo(name = "type") val type: FlipperFileType? = path.fileType,
    @ColumnInfo(name = "content") val content: DatabaseKeyContent,
    @ColumnInfo(name = "deleted") val deleted: Boolean = false,
    @ColumnInfo(name = "notes") val notes: String? = null,
)
