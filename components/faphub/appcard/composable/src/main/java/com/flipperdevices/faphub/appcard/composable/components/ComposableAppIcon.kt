package com.flipperdevices.faphub.appcard.composable.components

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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.image.FlipperAsyncImage
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposableAppIcon(
    url: String?,
    description: String?,
    modifier: Modifier = Modifier
) {
    var isPlaceholderActive by remember { mutableStateOf(true) }

    val modifierWithPlaceholder = if (isPlaceholderActive) {
        modifier.placeholderConnecting()
    } else {
        modifier.border(1.dp, LocalPallet.current.text16, RoundedCornerShape(6.dp))
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
                contentDescription = description,
                onLoading = { isPlaceholderActive = it },
                filterQuality = FilterQuality.None,
                colorFilter = ColorFilter.tint(LocalPallet.current.text100)
            )
        }
    }
}
