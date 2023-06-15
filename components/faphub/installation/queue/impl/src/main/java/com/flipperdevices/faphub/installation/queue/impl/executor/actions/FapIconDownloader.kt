package com.flipperdevices.faphub.installation.queue.impl.executor.actions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.inject.Inject

private const val IMAGE_QUALITY = 100

class FapIconDownloader @Inject constructor(
    private val context: Context
) : LogTagProvider {
    override val TAG = "FapIconDownloader"

    suspend fun downloadToBase64(picUrl: String): Result<String> = runCatching {
        info { "Download $picUrl" }
        val request = ImageRequest.Builder(context)
            .data(picUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(picUrl)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(picUrl)
            .build()

        val response = context.imageLoader.execute(request)
        info { "Recieve $response" }
        val drawable = response.drawable ?: error("Image is empty")
        val bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            val bounds = drawable.bounds
            val createdBitmap = Bitmap.createBitmap(
                bounds.width(),
                bounds.height(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(createdBitmap)
            drawable.draw(canvas)
            createdBitmap
        }
        val base64 = ByteArrayOutputStream().use { tmpStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, tmpStream)
            Base64.getEncoder().encodeToString(tmpStream.toByteArray())
        }
        return@runCatching base64
    }
}
