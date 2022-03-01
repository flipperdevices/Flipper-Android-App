package com.flipperdevices.bridge.dao.api.model

/**
 * The most complete description of the key
 */
data class FlipperKey(
    val path: FlipperKeyPath,
    val keyContent: FlipperKeyContent,
    val notes: String? = null
)
