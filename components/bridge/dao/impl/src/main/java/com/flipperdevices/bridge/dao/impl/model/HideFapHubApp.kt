package com.flipperdevices.bridge.dao.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "faphub_hide_app"
)
data class HideFapHubApp(
    @ColumnInfo(name = "uid")
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    @ColumnInfo(name = "app_uid") val applicationUid: String
)
