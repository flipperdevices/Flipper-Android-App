package com.flipperdevices.bridge.dao.impl.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperKey
import java.io.File

/**
 * Helper for working with flipper key content
 * Can save content to device storage and return link to it
 */
interface FlipperKeyContentConverter {
    suspend fun extractFile(flipperKey: FlipperKey): File
}
