package com.flipperdevices.wearable.sync.wear.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.wearable.sync.common.WearableSyncItem
import java.io.File

/**
 * Analog for FlipperKey, but for wearable device
 */
data class FlipperWearKey(
    val path: FlipperKeyPath,
    val isFavorites: Boolean
) {
    constructor(syncItem: WearableSyncItem) : this(
        path = File(syncItem.path).absoluteFile.let {
            FlipperKeyPath(
                path = FlipperFilePath(
                    folder = it.parent.orEmpty(),
                    nameWithExtension = it.name
                ),
                deleted = false
            )
        },
        isFavorites = syncItem.data.isFavorite
    )
}
