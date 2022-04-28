package com.flipperdevices.updater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VersionFiles(
    val version: FirmwareVersion,
    val updaterFile: DistributionFile
) : Parcelable

@Parcelize
data class DistributionFile(
    val url: String,
    val sha256: String
) : Parcelable
