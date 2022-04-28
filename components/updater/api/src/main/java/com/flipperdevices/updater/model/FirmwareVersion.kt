package com.flipperdevices.updater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirmwareVersion(
    val channel: FirmwareChannel,
    val version: String,
    val buildDate: String? = null
) : Parcelable
