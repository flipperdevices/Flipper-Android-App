package com.flipperdevices.bridge.dao.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

@Entity(
    tableName = "keys",
    indices = [Index(value = ["path"], unique = true)]
)
data class Key(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "path") val path: FlipperKeyPath,
    @ColumnInfo(name = "type") val type: FlipperFileType? = path.fileType,
    @ColumnInfo(name = "file_path") val filePath: String
)
