package com.flipperdevices.faphub.appcard.composable.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.FlipperAsyncImage
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
internal fun ComposableAppIcon(
    modifier: Modifier = Modifier,
    url: String,
    description: String?
) {
    var isPlaceholderActive by remember { mutableStateOf(true) }
    val modifierWithClip = modifier
        .clip(RoundedCornerShape(6.dp))
        .size(42.dp)
        .background(LocalPallet.current.accent)

    val modifierWithPlaceholder = if (isPlaceholderActive) {
        modifierWithClip.placeholderConnecting()
    } else modifierWithClip
    Box(
        modifier = modifierWithPlaceholder,
    ) {
        FlipperAsyncImage(
            modifier = Modifier
                .padding(all = 4.dp)
                .fillMaxSize(),
            url = url,
            contentDescription = description,
            onLoading = { isPlaceholderActive = it },
            enableDiskCache = false,
            enableMemoryCache = false,
            filterQuality = FilterQuality.None
        )
    }
}