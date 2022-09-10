package com.flipperdevices.wearable.sync.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class WearableSyncItem(
    val path: String,
    val data: WearableSyncItemData
)

@Parcelize
data class WearableSyncItemData(
    val isFavorite: Boolean
) : Parcelable
