package com.flipperdevices.core.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

private const val SHARE_DIR = "/sharedkeys"

object ShareHelper {
    fun shareFile(context: Context, file: File, name: String, resId: Int) {
        processFile(file, context)
        val uri = FileProvider.getUriForFile(
            context,
            BuildConfig.SHARE_FILE_AUTHORITIES,
            file,
            name
        )
        val intent = Intent(Intent.ACTION_SEND, uri).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        val activityIntent = Intent.createChooser(
            intent,
            context.getString(resId, name)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(activityIntent)
    }

    private fun processFile(file: File, context: Context) {
        val fileFolder = file.parentFile?.path
        val cacheFolder = "${context.cacheDir}$SHARE_DIR"
        if (fileFolder == null || fileFolder != cacheFolder) {
            error("File not exist in app cache")
        }
    }
}
