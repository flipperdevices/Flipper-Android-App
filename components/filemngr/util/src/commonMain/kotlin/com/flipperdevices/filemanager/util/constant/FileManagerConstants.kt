package com.flipperdevices.filemanager.util.constant

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType

object FileManagerConstants {
    const val FILE_NAME_AVAILABLE_CHARACTERS = "“0-9”, “A-Z”, “a-z”, “!#\\\$%&'()-@^_`{}~”"

    val FILE_EXTENSION_HINTS = listOf("txt").plus(FlipperKeyType.entries.map { it.extension })

    /**
     * Max file of size available to edit
     */
    const val LIMITED_SIZE_BYTES = 1024L * 1024L // 1MB
}
