package com.flipperdevices.core.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object ShareHelper {
    fun shareFile(context: Context, file: File, name: String, resId: Int) {
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
}
