package com.flipperdevices.wearable.emulate.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import java.io.File

data class KeyToEmulate(
    val keyPath: String
) {
    val keyType: FlipperKeyType?
        get() = FlipperKeyType.getByExtension(File(keyPath).extension)
}
