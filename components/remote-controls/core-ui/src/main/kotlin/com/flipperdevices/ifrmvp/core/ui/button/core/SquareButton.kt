package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf
import com.flipperdevices.ifrmvp.core.ui.util.GridConstants

@Composable
fun SquareButton(
    onClick: (() -> Unit)?,
    background: Color,
    isEmulating: Boolean,
    isSyncing: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .size(GridConstants.DEFAULT_BUTTON_SIZE.sf)
            .clip(RoundedCornerShape(8.sf))
            .background(background)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick,
                        enabled = !isEmulating
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center,
        content = {
            content.invoke(this)
            EmulatingBox(isEmulating = isEmulating)
            SyncingBox(isSyncing = isSyncing)
        }
    )
}

@Composable
internal fun EmulatingBox(
    isEmulating: Boolean,
    modifier: Modifier = Modifier
) {
    Crossfade(isEmulating) { isEmulating ->
        if (isEmulating) {
            Box(
                modifier
                    .fillMaxSize()
                    .placeholderConnecting()
            )
        }
    }
}

@Composable
internal fun SyncingBox(
    isSyncing: Boolean,
    modifier: Modifier = Modifier
) {
    Crossfade(isSyncing) { isEmulating ->
        if (isEmulating) {
            Box(
                modifier
                    .fillMaxSize()
                    .background(LocalPalletV2.current.surface.menu.body.dufault)
            )
            Box(
                modifier
                    .fillMaxSize()
                    .placeholderConnecting()
            )
        }
    }
}
