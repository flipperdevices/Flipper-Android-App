package com.flipperdevices.updater.model

import kotlinx.serialization.Serializable

data class VersionFiles(
    val version: FirmwareVersion,
    val updaterFile: DistributionFile,
    val changelog: String? = null
)

@Serializable
data class DistributionFile(
    val url: String,
    val sha256: String? = null
)
