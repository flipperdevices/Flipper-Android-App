package com.flipperdevices.core.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.flipperdevices.core.ktx.jre.createNewFileWithMkDirs

object ShareHelper {
    fun shareRawFile(context: Context, data: ByteArray, resId: Int, name: String) {
        val file = SharableFile(
            nameFile = name,
            context = context
        ).apply { createNewFileWithMkDirs() }

        file.outputStream().use { fileStream ->
            fileStream.write(data)
        }

        shareFile(
            context = context,
            file = file,
            resId = resId
        )
    }
    fun shareFile(context: Context, file: SharableFile, resId: Int) {
        val uri = FileProvider.getUriForFile(
            context,
            BuildConfig.SHARE_FILE_AUTHORITIES,
            file,
            file.name
        )
        val intent = Intent(Intent.ACTION_SEND, uri).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        val activityIntent = Intent.createChooser(
            intent,
            context.getString(resId, file.name)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(activityIntent)
    }

    fun shareText(
        context: Context,
        title: String,
        text: String
    ) {
        val intent = Intent(Intent.ACTION_SEND)

        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.putExtra(Intent.EXTRA_TITLE, title)

        val chooserIntent = Intent.createChooser(intent, null)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
