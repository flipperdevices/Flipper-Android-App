package com.flipperdevices.keyemulate.composable.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.impl.R
import com.flipperdevices.keyemulate.model.LoadingState
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.foundation.placeholder
import io.github.fornewid.placeholder.foundation.shimmer

@Composable
fun ComposableActionLoading(
    loadingState: LoadingState?,
    modifier: Modifier = Modifier
) {
    val placeholderColor = LocalPallet.current.text8.copy(alpha = 0.2f)
    InternalComposableEmulateButtonWithText(
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
            null -> null
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
                ComposableActionLoading(it)
            }
        }
    }
}
