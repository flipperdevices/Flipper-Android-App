package com.flipperdevices.wearable.sync.common

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.flipperdevices.core.ktx.android.fromBytes
import com.google.android.gms.wearable.DataItem
import kotlinx.parcelize.Parcelize

@Stable
data class WearableSyncItem(
    val path: String,
    val data: WearableSyncItemData
) {
    companion object {
        fun fromDataItem(dataItem: DataItem): WearableSyncItem? {
            val path = dataItem.uri.path ?: return null
            val data = fromBytes<WearableSyncItemData>(dataItem.data) ?: return null
            return WearableSyncItem(path, data)
        }
    }
}

@Parcelize
data class WearableSyncItemData(
    val isFavorite: Boolean
) : Parcelable
