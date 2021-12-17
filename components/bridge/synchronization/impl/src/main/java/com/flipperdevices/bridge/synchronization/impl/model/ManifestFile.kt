package com.flipperdevices.bridge.synchronization.impl.model

import kotlinx.serialization.Serializable

@Serializable
data class ManifestFile(val keys: List<KeyWithHash>)
