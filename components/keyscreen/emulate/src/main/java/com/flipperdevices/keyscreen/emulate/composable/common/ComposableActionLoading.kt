package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.ktx.animatedDots
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.emulate.R
import com.flipperdevices.keyscreen.emulate.model.LoadingState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun ComposableActionLoading(
    modifier: Modifier,
    loadingState: LoadingState
) {
    val placeholderColor = LocalPallet.current.text8.copy(alpha = 0.2f)
    ComposableEmulateButton(
        modifier = modifier,
        buttonModifier = modifier.placeholder(
            visible = true,
            color = placeholderColor,
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = LocalPallet.current.placeholder
            )
        ),
        buttonContent = {},
        underButtonContent = {
            val textId = when (loadingState) {
                LoadingState.CONNECTING -> R.string.emulate_loading_connecting
                LoadingState.SYNCING -> R.string.emulate_loading_syncing
            }
            Text(
                text = stringResource(textId) + animatedDots(),
                style = LocalTypography.current.bodyR14,
                color = LocalPallet.current.text30
            )
        },
        borderColor = null,
        backgroundBrush = SolidColor(placeholderColor)
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableLoadingPreview() {
    FlipperThemeInternal {
        Column() {
            LoadingState.values().forEach {
                ComposableActionLoading(Modifier, it)
            }
        }
    }
}
