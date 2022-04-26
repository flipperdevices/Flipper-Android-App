package com.flipperdevices.updater.model

data class FirmwareVersion(
    val channel: FirmwareChannel,
    val version: String,
    val buildDate: String? = null
)
