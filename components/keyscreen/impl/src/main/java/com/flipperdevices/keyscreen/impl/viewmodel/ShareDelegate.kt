package com.flipperdevices.keyscreen.impl.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.flipperdevices.bridge.dao.api.delegates.KeyParser
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.flipperdevices.filemanager.api.share.ShareApi
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val BITMAP_QUALITY = 100
private const val EXPECTED_BITMAP_WIDTH = 512
private const val EXPECTED_BITMAP_HEIGHT = 512

class ShareDelegate(
    private val context: Context,
    private val shareApi: ShareApi,
    private val keyParser: KeyParser
) : LogTagProvider {
    override val TAG = "ShareDelegate"

    suspend fun share(flipperKey: FlipperKey) {
        val link = keyParser.keyToUrl(flipperKey)
        // TODO catch exception from bitmap decoding
        val icon = flipperKey.path.fileType?.let {
            shareIcon(it)
        }
        info { "Share ${flipperKey.path.name} with icon uri $icon and link $link" }
        shareLink(flipperKey, link, icon)
    }

    private suspend fun shareIcon(
        fileType: FlipperFileType
    ): Uri = withContext(Dispatchers.IO) {
        val bitmap = getBitmap(fileType.icon)!!
        return@withContext FlipperStorageProvider.useTemporaryFolder(context) {
            val iconFile = File(it, "${fileType.extension}.png")

            iconFile.outputStream().use { outputStream ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    BITMAP_QUALITY,
                    outputStream
                )
            }

            return@useTemporaryFolder shareApi.getExternalUriForFile(
                context, iconFile
            )
        }
    }

    private suspend fun shareLink(
        flipperKey: FlipperKey,
        link: String,
        shareIconUri: Uri?
    ) = withContext(Dispatchers.Main) {
        val intent = Intent(Intent.ACTION_SEND)

        intent.putExtra(Intent.EXTRA_TEXT, link)
        intent.putExtra(Intent.EXTRA_TITLE, flipperKey.path.name)
        if (shareIconUri != null) {
            // TODO preview icon doesn't show
            intent.data = shareIconUri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(intent, null)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }

    private suspend fun getBitmap(
        @DrawableRes resourceId: Int
    ) = withContext(Dispatchers.Default) {
        val drawable: Drawable? = ContextCompat.getDrawable(context, resourceId)
        if (drawable is BitmapDrawable) {
            return@withContext drawable.bitmap
        }
        if (drawable !is VectorDrawable) {
            return@withContext null
        }
        val bitmap = Bitmap.createBitmap(
            drawable.getIntrinsicWidth(),
            drawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return@withContext bitmap
    }
}
