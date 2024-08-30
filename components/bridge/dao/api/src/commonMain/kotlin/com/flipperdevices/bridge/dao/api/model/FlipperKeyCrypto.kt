package com.flipperdevices.bridge.dao.api.model

import kotlinx.serialization.Serializable

@Serializable
data class FlipperKeyCrypto(
    val fileId: String,
    val pathToKey: String,
    val cryptoKey: String
)
