package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.emulate.R
import com.flipperdevices.keyscreen.emulate.model.LoadingState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun ComposableActionLoading(
    modifier: Modifier = Modifier,
    loadingState: LoadingState
) {
    val placeholderColor = LocalPallet.current.text8.copy(alpha = 0.2f)
    ComposableEmulateButtonWithText(
        modifier = modifier,
        buttonModifier = Modifier.placeholder(
            visible = true,
            color = placeholderColor,
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = LocalPallet.current.placeholder
            ),
            shape = RoundedCornerShape(16.dp)
        ),
        buttonTextId = R.string.keyscreen_loading,
        color = LocalPallet.current.text8,
        textId = when (loadingState) {
            LoadingState.CONNECTING -> R.string.emulate_loading_connecting
            LoadingState.SYNCING -> R.string.emulate_loading_syncing
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableLoadingPreview() {
    FlipperThemeInternal {
        Column {
            LoadingState.values().forEach {
                ComposableActionLoading(Modifier, it)
            }
        }
    }
}
