package com.flipperdevices.info.impl.model

import android.content.Context
import android.text.format.Formatter

data class DeviceInfo(
    val firmwareVersion: FirmwareVersion? = null,
    val flashInt: StorageInfo? = null,
    val flashSd: StorageInfo? = null
)

data class VerboseDeviceInfo(
    val rpcInformationMap: Map<String, String> = emptyMap()
)

data class StorageInfo(
    val used: Long,
    val total: Long
) {
    fun toString(context: Context): String {
        val usedHumanReadable = Formatter.formatFileSize(context, used)
        val totalHumanReadable = Formatter.formatFileSize(context, total)

        return "$usedHumanReadable/$totalHumanReadable"
    }
}

sealed class FirmwareVersion(val buildDate: String? = null) {
    class Dev(
        val commitSHA: String,
        buildDate: String
    ) : FirmwareVersion(buildDate)

    class Release(
        val version: String,
        buildDate: String
    ) : FirmwareVersion(buildDate)

    class ReleaseCandidate(
        val version: String,
        buildDate: String
    ) : FirmwareVersion(buildDate)
}
