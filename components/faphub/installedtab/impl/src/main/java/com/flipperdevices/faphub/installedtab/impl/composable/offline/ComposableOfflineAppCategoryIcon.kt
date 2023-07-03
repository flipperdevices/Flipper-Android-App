package com.flipperdevices.faphub.installedtab.impl.composable.offline

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
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.FlipperAsyncImage
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposableOfflineAppCategoryIcon(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    var isFailedLoaded by remember { mutableStateOf(false) }

    if (isFailedLoaded) {
        return
    }

    var iconModifier = modifier.size(14.dp)

    var isPlaceholderActive by remember { mutableStateOf(true) }

    iconModifier = if (isPlaceholderActive) {
        iconModifier.placeholderConnecting()
    } else {
        iconModifier
    }

    Box(
        modifier = iconModifier
    ) {
        FlipperAsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            url = null,
            contentDescription = categoryName,
            colorFilter = ColorFilter.tint(LocalPallet.current.text60),
            filterQuality = FilterQuality.None,
            onLoading = { isPlaceholderActive = it },
            cacheKey = remember(categoryName) { categoryName.lowercase() },
            onError = { isFailedLoaded = true }
        )
    }
}