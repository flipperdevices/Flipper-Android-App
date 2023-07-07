package com.flipperdevices.faphub.appcard.composable.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun ComposableAppScreenshot(
    url: String?,
    modifier: Modifier = Modifier
) {
    var isPlaceholderActive by remember { mutableStateOf(true) }
    var modifierWithClip = modifier
        .clip(RoundedCornerShape(8.dp))

    if (url != null) {
        modifierWithClip = modifierWithClip
            .border(
                BorderStroke(1.dp, LocalPallet.current.fapScreenshotBorder),
                RoundedCornerShape(8.dp)
            )
            .background(LocalPallet.current.accent)
    }

    val modifierWithPlaceholder = if (isPlaceholderActive) {
        modifierWithClip.placeholderConnecting()
    } else {
        modifierWithClip
    }
    Box(
        modifier = modifierWithPlaceholder
    ) {
        if (url != null) {
            FlipperAsyncImage(
                modifier = Modifier
                    .padding(all = 4.dp)
                    .fillMaxSize(),
                url = url,
                contentDescription = null,
                onLoading = { isPlaceholderActive = it },
                filterQuality = FilterQuality.None
            )
        }
    }
}
