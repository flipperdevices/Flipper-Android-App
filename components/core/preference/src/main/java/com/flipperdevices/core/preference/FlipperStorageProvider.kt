package com.flipperdevices.core.preference

import android.content.Context
import java.io.File

object FlipperStorageProvider {
    private const val SHARE_DIR = "sharedkeys/"
    private const val KEYS_DIR = "keysfiles/"
    internal const val DATASTORE_FILENAME_SETTINGS = "settings.pb"
    internal const val DATASTORE_FILENAME_PAIR_SETTINGS = "pair_settings.pb"

    fun getSharedKeyFolder(context: Context): File {
        return File(context.cacheDir, SHARE_DIR)
    }

    fun getKeyFolder(context: Context): File {
        return File(context.filesDir, KEYS_DIR)
    }
}
