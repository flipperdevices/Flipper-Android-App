package com.flipperdevices.updater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateRequest(
    val updateFrom: FirmwareVersion,
    val updateTo: VersionFiles,
    val requestId: Long = System.currentTimeMillis()
) : Parcelable
