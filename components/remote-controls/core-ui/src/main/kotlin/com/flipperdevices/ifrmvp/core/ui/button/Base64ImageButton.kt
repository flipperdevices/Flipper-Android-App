package com.flipperdevices.ifrmvp.core.ui.button

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.flipperdevices.ifrmvp.core.ui.button.core.SquareImageButton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val PNG_BASE64_HEADER = "data:image/png;base64,"

@OptIn(ExperimentalEncodingApi::class)
private fun resolveImage(imgBase64: String?): ByteArray? {
    if (imgBase64 == null) return null
    return runCatching {
        when {
            imgBase64.startsWith(PNG_BASE64_HEADER) -> {
                val base64WithoutHeader = imgBase64.replaceFirst(PNG_BASE64_HEADER, "")
                Base64.Default.decode(base64WithoutHeader)
            }

            else -> {
                println("Unknown image format: '${imgBase64.take(20)}'")
                Base64.Default.decode(imgBase64)
            }
        }
    }.onFailure { error("Could not resolve image from base 64 string.") }.getOrNull()
}

private fun imageBitmapFromBytes(encodedImageData: ByteArray): ImageBitmap? {
    return BitmapFactory.decodeByteArray(encodedImageData, 0, encodedImageData.size)?.asImageBitmap()
}

private fun toImageBitmap(base64Icon: String): ImageBitmap? {
    val byteArray = resolveImage(base64Icon) ?: return null
    return imageBitmapFromBytes(byteArray)
}

@Composable
fun rememberImageBitmap(base64Image: String): ImageBitmap? {
    if (base64Image.isBlank()) return null
    return remember { toImageBitmap(base64Image) }
}

@Composable
fun Base64ImageButton(base64Icon: String, onClick: () -> Unit) {
    val imageBitmap = rememberImageBitmap(base64Icon)
    if (imageBitmap != null) {
        SquareImageButton(
            onClick = onClick,
            background = Color(0xFF303030),
            bitmap = imageBitmap,
            iconTint = Color.Unspecified
        )
    }
}