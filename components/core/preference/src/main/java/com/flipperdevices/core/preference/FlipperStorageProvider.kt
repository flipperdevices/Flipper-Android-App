package com.flipperdevices.core.preference

import android.content.Context
import java.io.File

object FlipperStorageProvider {
    fun getKeyFolder(context: Context): File {
        return File(context.filesDir, "keysfiles/")
    }
}
