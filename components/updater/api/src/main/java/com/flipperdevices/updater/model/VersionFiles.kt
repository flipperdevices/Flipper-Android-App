package com.flipperdevices.updater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VersionFiles constructor(
    val version: FirmwareVersion,
    val updaterFile: DistributionFile,
    val changelog: String? = null
) : Parcelable

@Parcelize
data class DistributionFile(
    val url: String,
    val sha256: String
) : Parcelable
