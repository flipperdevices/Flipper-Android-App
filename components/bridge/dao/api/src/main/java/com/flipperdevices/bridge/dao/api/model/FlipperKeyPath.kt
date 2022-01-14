package com.flipperdevices.bridge.dao.api.model

import java.io.File

/**
 * Describe name and path for key
 */
data class FlipperKeyPath(
    val folder: String,
    val name: String, // With extension
) {
    val pathToKey: String by lazy { File(folder, name).path }
    val fileType: FlipperFileType? by lazy {
        FlipperFileType.getByExtension(
            name.substringAfterLast(
                "."
            )
        )
    }

    companion object {
        val DUMMY by lazy { FlipperKeyPath(FlipperFileType.NFC.flipperDir, "Test_Key.nfc") }
    }
}
