package com.flipperdevices.bridge.dao.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import java.io.File

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
    @ColumnInfo(name = "uid")
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    @ColumnInfo(name = "path") val path: String,
    // Denormalize for performance
    @ColumnInfo(name = "type") val type: FlipperKeyType?,
    @ColumnInfo(name = "content") val content: DatabaseKeyContent,
    @ColumnInfo(name = "deleted") val deleted: Boolean,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "synchronized_status")
    val synchronizedStatus: SynchronizedStatus = SynchronizedStatus.SYNCHRONIZED
) {
    val mainFilePath: FlipperFilePath
        get() {
            val pathNotNull = path
            val file = File(pathNotNull)
            return FlipperFilePath(file.parent.orEmpty(), file.name)
        }
}
