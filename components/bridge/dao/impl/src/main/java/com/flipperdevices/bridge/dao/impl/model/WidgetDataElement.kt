package com.flipperdevices.bridge.dao.impl.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.flipperdevices.bridge.dao.api.model.WidgetType

@Entity(
    tableName = "widgets",
    indices = [
        Index(value = ["key_id"]) // For performance
    ],
    foreignKeys = [
        ForeignKey(
            entity = Key::class,
            parentColumns = arrayOf("uid"),
            childColumns = arrayOf("key_id")
        )
    ]
)
data class WidgetDataElement(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "key_id") val keyId: Int? = null,
    @ColumnInfo(name = "type") val widgetType: WidgetType = WidgetType.SIMPLE
)