package com.flipperdevices.updater.model

data class VersionFiles(
    val version: FirmwareVersion,
    val updaterFile: DistributionFile
)

data class DistributionFile(
    val url: String,
    val sha256: String
)
