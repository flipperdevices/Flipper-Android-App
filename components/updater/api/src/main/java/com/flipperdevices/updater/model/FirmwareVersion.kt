package com.flipperdevices.updater.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class FirmwareVersion(
    val channel: FirmwareChannel,
    val version: String,
    val buildDate: String? = null
) : Parcelable
