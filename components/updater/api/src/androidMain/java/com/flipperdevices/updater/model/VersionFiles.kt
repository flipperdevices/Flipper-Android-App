package com.flipperdevices.updater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class VersionFiles constructor(
    val version: FirmwareVersion,
    val updaterFile: DistributionFile,
    val changelog: String? = null
) : Parcelable

@Parcelize
@Serializable
data class DistributionFile(
    val url: String,
    val sha256: String? = null
) : Parcelable
