package com.flipperdevices.wearable.sync.common

import androidx.compose.runtime.Stable
import com.google.android.gms.wearable.DataItem
import com.google.protobuf.InvalidProtocolBufferException

@Stable
data class WearableSyncItem(
    val path: String,
    val data: WearableSyncItemData
) {
    companion object {
        fun fromDataItem(dataItem: DataItem): WearableSyncItem? {
            val path = dataItem.uri.path ?: return null
            val data = try {
                WearableSyncItemData.parseFrom(dataItem.data)
            } catch (
                @Suppress("SwallowedException") throwable: InvalidProtocolBufferException
            ) {
                return null
            }
            return WearableSyncItem(path, data)
        }
    }
}
