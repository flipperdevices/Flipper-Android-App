package com.flipperdevices.core.ui.ktx.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun FlipperAsyncImage(
    url: String,
    contentDescription: String?,
    onLoading: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    filterQuality: FilterQuality = FilterQuality.None,
    contentScale: ContentScale = ContentScale.FillBounds,
    enableDiskCache: Boolean = false,
    enableMemoryCache: Boolean = false,
    colorFilter: ColorFilter? = null
) {
    val context = LocalContext.current
    val request = remember(url, enableDiskCache, enableMemoryCache) {
        ImageRequest.Builder(context)
            .data(url)
            .diskCachePolicy(
                if (enableDiskCache) {
                    CachePolicy.ENABLED
                } else {
                    CachePolicy.READ_ONLY
                }
            )
            .diskCacheKey(url)
            .memoryCachePolicy(
                if (enableMemoryCache) {
                    CachePolicy.ENABLED
                } else {
                    CachePolicy.READ_ONLY
                }
            )
            .memoryCacheKey(url)
            .transformations(WhiteToAlphaTransformation())
            .build()
    }
    AsyncImage(
        modifier = modifier,
        model = request,
        contentDescription = contentDescription,
        filterQuality = filterQuality,
        contentScale = contentScale,
        colorFilter = colorFilter,
        onLoading = { onLoading(true) },
        onSuccess = { onLoading(false) },
        onError = { onLoading(false) }
    )
}
