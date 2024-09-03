package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf

@Composable
internal fun EmulatingBox(
    modifier: Modifier = Modifier,
    isEmulating: Boolean = LocalButtonPlaceholder.current.isEmulating,
) {
    Crossfade(isEmulating) { isEmulatingLocal ->
        if (isEmulatingLocal) {
            Box(
                modifier
                    .fillMaxSize()
                    .placeholderConnecting()
            )
        }
    }
}

@Composable
internal fun NoConnectionBox(
    modifier: Modifier = Modifier,
    isConnected: Boolean = LocalButtonPlaceholder.current.isConnected,
) {
    Crossfade(isConnected) { isConnectedLocal ->
        if (!isConnectedLocal) {
            Box(
                modifier
                    .fillMaxSize()
                    .background(LocalPalletV2.current.action.blackAndWhite.icon.disabled.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
internal fun SyncingBox(
    modifier: Modifier = Modifier,
    isSyncing: Boolean = LocalButtonPlaceholder.current.isSyncing,
) {
    Crossfade(
        targetState = isSyncing,
        modifier = modifier
    ) { isEmulating ->
        if (isEmulating) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(LocalPalletV2.current.surface.menu.body.dufault)
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .placeholderConnecting()
            )
        }
    }
}

@Preview
@Composable
private fun SyncingBoxPreview() {
    FlipperThemeInternal {
        Box(modifier = Modifier.size(54.sf).background(LocalPalletV2.current.surface.bottomBar.body))
        SyncingBox()
    }
}

@Preview
@Composable
private fun NoConnectionBoxPreview() {
    FlipperThemeInternal {
        Box(modifier = Modifier.size(54.sf).background(LocalPalletV2.current.surface.bottomBar.body))
        NoConnectionBox()
    }
}

@Preview
@Composable
private fun EmulatingBoxPreview() {
    FlipperThemeInternal {
        Box(modifier = Modifier.size(54.sf).background(LocalPalletV2.current.surface.bottomBar.body))
        EmulatingBox()
    }
}
