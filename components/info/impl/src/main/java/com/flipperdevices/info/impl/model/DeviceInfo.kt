package com.flipperdevices.info.impl.model

import android.content.Context
import android.text.format.Formatter
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.info.impl.R
import com.flipperdevices.updater.model.FirmwareVersion
import kotlin.math.max

data class DeviceInfo(
    val firmwareVersion: FirmwareVersion? = null,
    val flashInt: StorageStats? = null,
    val flashSd: StorageStats? = null
)

data class VerboseDeviceInfo(
    val rpcInformationMap: Map<String, String> = emptyMap()
)

fun StorageStats.toString(context: Context): String {
    return when (this) {
        StorageStats.Error -> {
            context.getString(R.string.info_device_info_flash_not_found)
        }
        is StorageStats.Loaded -> {
            val usedHumanReadable = Formatter.formatFileSize(
                context,
                max(0L, total - free)
            )
            val totalHumanReadable = Formatter.formatFileSize(context, total)

            "$usedHumanReadable/$totalHumanReadable"
        }
    }
}
