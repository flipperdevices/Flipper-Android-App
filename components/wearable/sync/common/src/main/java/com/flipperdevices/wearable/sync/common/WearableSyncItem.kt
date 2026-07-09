package com.flipperdevices.wearable.sync.common

import androidx.compose.runtime.Stable
import com.google.android.gms.wearable.DataItem
import java.io.IOException

@Stable
data class WearableSyncItem(
    val path: String,
    val data: WearableSyncItemData
) {
    companion object {
        @Suppress("SwallowedException")
        fun fromDataItem(dataItem: DataItem): WearableSyncItem? {
            val path = dataItem.uri.path ?: return null
            val bytes = dataItem.data ?: return null
            val data = try {
                WearableSyncItemData.ADAPTER.decode(bytes)
            } catch (_: IOException) {
                return null
            }
            return WearableSyncItem(path, data)
        }
    }
}
