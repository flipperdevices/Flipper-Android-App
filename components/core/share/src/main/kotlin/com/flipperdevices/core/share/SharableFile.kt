package com.flipperdevices.core.share

import android.content.Context
import java.io.File

private const val SHARE_DIR = "sharedkeys/"

data class SharableFile(
    private val context: Context,
    private val nameFile: String
) : File(context.cacheDir, "$SHARE_DIR$nameFile")
