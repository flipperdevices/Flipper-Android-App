package com.flipperdevices.info.impl.model

data class DeviceInfo(
    val firmwareVersion: FirmwareVersion? = null,
    val flashInt: StorageInfo? = null,
    val flashSd: StorageInfo? = null
)

data class StorageInfo(
    val used: Long,
    val total: Long
)

sealed class FirmwareVersion {
    data class Dev(
        val commitSHA: String,
        val buildDate: String
    ) : FirmwareVersion()

    data class Release(
        val version: String,
        val buildDate: String
    ) : FirmwareVersion()

    data class ReleaseCandidate(
        val version: String,
        val buildDate: String
    ) : FirmwareVersion()

    object Unknown : FirmwareVersion()
}
