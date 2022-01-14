package com.flipperdevices.bridge.dao.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.flipperdevices.bridge.dao.api.model.FlipperFileType

@Entity(
    tableName = "keys",
    indices = [Index(value = ["name", "type"], unique = true)]
)
data class Key(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val fileType: FlipperFileType,
    @ColumnInfo(name = "file_path") val filePath: String
)
