package com.flipperdevices.ifrmvp.core.ui.button.core

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPalletV2
import com.flipperdevices.ifrmvp.core.ui.layout.core.sf

@Composable
fun ButtonPlaceholderBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = LocalButtonPlaceholder.current
    Box(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min)
    ) {
        content.invoke(this)
        Crossfade(state) { animatedState ->
            when (animatedState) {
                ButtonPlaceholderState.EMULATING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .placeholderConnecting(),
                    )
                }

                ButtonPlaceholderState.NOT_CONNECTED -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                LocalPalletV2.current.action.blackAndWhite.icon
                                    .disabled
                                    .copy(alpha = 0.5f)
                            ),
                    )
                }

                ButtonPlaceholderState.SYNCING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .placeholderConnecting(),
                    )
                }

                ButtonPlaceholderState.NONE -> Unit
            }
        }
    }
}

@Preview
@Composable
private fun SyncingBoxPreview() {
    FlipperThemeInternal {
        CompositionLocalProvider(
            value = LocalButtonPlaceholder provides ButtonPlaceholderState.SYNCING,
            content = {
                Box(
                    modifier = Modifier
                        .size(54.sf)
                        .background(Color.Magenta)
                )
            }
        )
    }
}

@Preview
@Composable
private fun NoConnectionBoxPreview() {
    FlipperThemeInternal {
        CompositionLocalProvider(
            value = LocalButtonPlaceholder provides ButtonPlaceholderState.NOT_CONNECTED,
            content = {
                Box(
                    modifier = Modifier
                        .size(54.sf)
                        .background(LocalPalletV2.current.surface.bottomBar.body)
                )
            }
        )
    }
}

@Preview
@Composable
private fun EmulatingBoxPreview() {
    FlipperThemeInternal {
        CompositionLocalProvider(
            value = LocalButtonPlaceholder provides ButtonPlaceholderState.EMULATING,
            content = {
                Box(
                    modifier = Modifier
                        .size(54.sf)
                        .background(LocalPalletV2.current.surface.bottomBar.body)
                )
            }
        )
    }
}
