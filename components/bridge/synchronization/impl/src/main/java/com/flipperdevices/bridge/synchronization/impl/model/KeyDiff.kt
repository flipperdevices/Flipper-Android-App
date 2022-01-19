package com.flipperdevices.bridge.synchronization.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

data class KeyDiff(
    val keyPath: FlipperKeyPath,
    val action: KeyAction
)
