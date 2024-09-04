package com.flipperdevices.bridge.connection.feature.storageinfo.model

import android.content.Context
import com.flipperdevices.bridge.connection.feature.storageinfo.api.R
import com.flipperdevices.core.ktx.jre.toFormattedSize
import kotlin.math.max

fun StorageStats.toString(context: Context): String {
    return when (this) {
        StorageStats.Error -> context.getString(R.string.info_device_info_flash_not_found)
        is StorageStats.Loaded -> {
            val usedHumanReadable = max(0L, total - free).toFormattedSize()
            val totalHumanReadable = total.toFormattedSize()

            "$usedHumanReadable / $totalHumanReadable"
        }
    }
}
