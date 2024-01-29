package com.flipperdevices.core.share

import android.content.Context
import java.io.File

private const val SHARE_DIR = "sharedkeys/"

class SharableFile(
    context: Context,
    nameFile: String
) : File(context.cacheDir, "$SHARE_DIR$nameFile")
