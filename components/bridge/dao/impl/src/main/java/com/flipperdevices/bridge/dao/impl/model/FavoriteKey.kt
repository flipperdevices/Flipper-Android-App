package com.flipperdevices.bridge.dao.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_keys",
    foreignKeys = [
        ForeignKey(
            entity = Key::class,
            parentColumns = arrayOf("uid"),
            childColumns = arrayOf("key_id")
        )
    ]
)
data class FavoriteKey(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "key_id", index = true) val keyId: Int = 0,
    @ColumnInfo(name = "order") val order: Int = 0
)