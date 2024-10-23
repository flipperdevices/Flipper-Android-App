package com.flipperdevices.core.ui.ktx.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.transformations

@Composable
fun FlipperAsyncImage(
    url: String,
    contentDescription: String?,
    onLoading: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    filterQuality: FilterQuality = FilterQuality.None,
    contentScale: ContentScale = ContentScale.FillBounds,
    colorFilter: ColorFilter? = null,
    cacheKey: String? = url,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) {
    val context = LocalContext.current
    val request = remember(context, url, cacheKey) {
        var builder = ImageRequest.Builder(context)
            .transformations(WhiteToAlphaTransformation())
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .data(url)

        if (cacheKey != null) {
            builder = builder.diskCacheKey(cacheKey)
                .memoryCacheKey(cacheKey)
        }
        builder.build()
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
        onError = {
            onLoading(false)
            onError?.invoke(it)
        }
    )
}
