package com.flipper.bridge.api.model

/**
 * Data class which represent information via flipper
 * like firmware version, device name and other
 */
data class FlipperGATTInformation(
    val deviceName: String? = null,
    val manufacturerName: String? = null,
    val hardwareRevision: String? = null,
    val softwareVersion: String? = null
)
