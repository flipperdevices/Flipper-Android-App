package com.flipperdevices.faphub.installedtab.impl.composable.offline

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.transformations
import com.flipperdevices.core.ui.ktx.image.WhiteToAlphaTransformation
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import java.io.File

@Composable
fun ComposableOfflineAppCategoryIcon(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cachedFile = remember(context, categoryName) {
        extractDataFromCache(context, categoryName)
    }

    var isFailedLoaded by remember { mutableStateOf(false) }

    if (cachedFile == null || isFailedLoaded) {
        return
    }

    var iconModifier = modifier.size(14.dp)

    var isPlaceholderActive by remember { mutableStateOf(true) }

    iconModifier = if (isPlaceholderActive) {
        iconModifier.placeholderConnecting()
    } else {
        iconModifier
    }

    val request = remember(cachedFile) {
        ImageRequest.Builder(context)
            .data(cachedFile)
            .transformations(WhiteToAlphaTransformation())
            .build()
    }

    Box(
        modifier = iconModifier
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = request,
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalPallet.current.text60),
            filterQuality = FilterQuality.None,
            contentScale = ContentScale.FillBounds,
            onLoading = { isPlaceholderActive = true },
            onSuccess = { isPlaceholderActive = false },
            onError = {
                isPlaceholderActive = false
                isFailedLoaded = false
            }
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
private fun extractDataFromCache(context: Context, cacheKey: String): File? {
    return context.imageLoader.diskCache?.openSnapshot(cacheKey)?.use {
        it.data.toFile()
    }
}
