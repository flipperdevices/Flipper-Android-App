package com.flipperdevices.core.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun FlipperAsyncImageWithPlaceholder(
    modifier: Modifier = Modifier,
    url: String,
    contentDescription: String?,
    filterQuality: FilterQuality = FilterQuality.None,
    contentScale: ContentScale = ContentScale.FillBounds,
    enableDiskCache: Boolean = false,
    enableMemoryCache: Boolean = true,
    colorFilter: ColorFilter? = null,
) {
    var isPlaceholderActive by remember { mutableStateOf(true) }
    val modifierWithPlaceholder = if (isPlaceholderActive) {
        modifier.placeholderConnecting()
    } else modifier
    FlipperAsyncImage(
        modifier = modifierWithPlaceholder,
        url = url,
        contentDescription = contentDescription,
        filterQuality = filterQuality,
        contentScale = contentScale,
        enableDiskCache = enableDiskCache,
        enableMemoryCache = enableMemoryCache,
        onLoading = { isPlaceholderActive = it },
        colorFilter = colorFilter
    )
}


@Composable
fun FlipperAsyncImage(
    modifier: Modifier = Modifier,
    url: String,
    contentDescription: String?,
    filterQuality: FilterQuality = FilterQuality.None,
    contentScale: ContentScale = ContentScale.FillBounds,
    enableDiskCache: Boolean = false,
    enableMemoryCache: Boolean = false,
    onLoading: (Boolean) -> Unit,
    colorFilter: ColorFilter? = null
) {
    val request = ImageRequest.Builder(LocalContext.current)
        .diskCachePolicy(
            if (enableDiskCache) {
                CachePolicy.ENABLED
            } else CachePolicy.READ_ONLY
        )
        .diskCacheKey(url)
        .memoryCachePolicy(
            if (enableMemoryCache) {
                CachePolicy.ENABLED
            } else CachePolicy.READ_ONLY
        )
        .memoryCacheKey(url)
        .build()

    AsyncImage(
        modifier = modifier,
        model = request,
        contentDescription = contentDescription,
        filterQuality = filterQuality,
        contentScale = contentScale,
        onLoading = { onLoading(true) },
        onSuccess = { onLoading(false) },
        onError = { onLoading(false) },
        colorFilter = colorFilter
    )
}