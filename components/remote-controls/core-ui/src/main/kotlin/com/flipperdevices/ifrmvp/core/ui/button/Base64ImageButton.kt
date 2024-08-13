package com.flipperdevices.ifrmvp.core.ui.button

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.flipperdevices.core.log.TaggedLogger
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.button.core.SquareImageButton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val PNG_BASE64_HEADER = "data:image/png;base64,"
private const val HEADER_LENGTH = 20

@OptIn(ExperimentalEncodingApi::class)
private fun resolveImage(imgBase64: String?): ByteArray? {
    val logger = TaggedLogger("Base64ImageButton")
    if (imgBase64 == null) return null
    return runCatching {
        when {
            imgBase64.startsWith(PNG_BASE64_HEADER) -> {
                val base64WithoutHeader = imgBase64.replaceFirst(PNG_BASE64_HEADER, "")
                Base64.Default.decode(base64WithoutHeader)
            }

            else -> {
                logger.warn {
                    "#resolveImage Unknown image format: '${imgBase64.take(HEADER_LENGTH)}'"
                }
                Base64.Default.decode(imgBase64)
            }
        }
    }
        .onFailure { throwable ->
            logger.error(throwable) {
                "#resolveImage Could not resolve image from base 64 string."
            }
        }
        .getOrNull()
}

private fun imageBitmapFromBytes(encodedImageData: ByteArray): ImageBitmap? {
    return BitmapFactory.decodeByteArray(encodedImageData, 0, encodedImageData.size)
        ?.asImageBitmap()
}

private fun toImageBitmap(base64Icon: String): ImageBitmap? {
    val byteArray = resolveImage(base64Icon) ?: return null
    return imageBitmapFromBytes(byteArray)
}

@Composable
fun rememberImageBitmap(base64Image: String): ImageBitmap? {
    if (base64Image.isBlank()) return null
    return remember(base64Image) { toImageBitmap(base64Image) }
}

@Composable
fun Base64ImageButton(
    base64Icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val imageBitmap = rememberImageBitmap(base64Icon)
    if (imageBitmap != null) {
        SquareImageButton(
            onClick = onClick,
            background = LocalPalletV2.current.surface.menu.body.dufault,
            bitmap = imageBitmap,
            iconTint = Color.Unspecified,
            modifier = modifier,
        )
    } else {
        UnknownButton(
            onClick = onClick,
        )
    }
}
