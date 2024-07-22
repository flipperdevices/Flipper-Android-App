package com.flipperdevices.bridge.dao.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import java.io.File

@Entity(
    tableName = "flipper_files",
    foreignKeys = [
        ForeignKey(
            entity = Key::class,
            parentColumns = arrayOf("uid"),
            childColumns = arrayOf("key_id"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["path", "key_id"],
            unique = true
        )
    ]
)
data class FlipperAdditionalFile(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "content") val content: DatabaseKeyContent,
    @ColumnInfo(name = "key_id", index = true) val keyId: Int
) {
    val filePath: FlipperFilePath
        get() {
            val file = File(path)
            return FlipperFilePath(file.parent.orEmpty(), file.name)
        }
}
