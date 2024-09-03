package com.flipperdevices.updater.model

import kotlinx.serialization.Serializable

@Serializable
data class FirmwareVersion(
    val channel: FirmwareChannel,
    val version: String,
    val buildDate: String? = null
)
