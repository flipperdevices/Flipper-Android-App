package com.flipperdevices.bridge.dao.api.model

import java.io.File
import kotlinx.serialization.Serializable

/**
 * Describe name and path for key
 */
@Serializable
data class FlipperKeyPath(
    val folder: String,
    val name: String // With extension
) {
    val pathToKey: String by lazy { File(folder, name).path }
    val fileType: FlipperFileType? by lazy {
        FlipperFileType.getByExtension(
            name.substringAfterLast(
                "."
            )
        )
    }

    override fun toString() = pathToKey

    companion object {
        val DUMMY by lazy { FlipperKeyPath(FlipperFileType.NFC.flipperDir, "Test_Key.nfc") }
    }
}
