package com.flipperdevices.updater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateRequest(
    val updateFrom: FirmwareVersion,
    val updateTo: FirmwareVersion,
    val changelog: String?,
    val content: DistributionFile,
    val requestId: Long = System.currentTimeMillis()
) : Parcelable
