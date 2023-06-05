package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.wearable.emulate.impl.R
import com.flipperdevices.wearable.emulate.impl.model.WearLoadingState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun ComposableActionLoading(
    keyEmulateApi: KeyEmulateUiApi,
    loadingState: WearLoadingState,
    modifier: Modifier = Modifier
) {
    val placeholderColor = LocalPallet.current.text8.copy(alpha = 0.2f)
    keyEmulateApi.ComposableEmulateButtonWithText(
        modifier = modifier,
        buttonModifier = Modifier.placeholder(
            visible = true,
            color = placeholderColor,
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = LocalPallet.current.placeholder
            ),
            shape = RoundedCornerShape(16.dp)
        ),
        buttonTextId = R.string.keyscreen_loading_btn,
        color = LocalPallet.current.text8,
        textId = when (loadingState) {
            WearLoadingState.FINDING_PHONE -> R.string.keyscreen_loading_find_phone
            WearLoadingState.CONNECTING_PHONE -> R.string.keyscreen_loading_connecting_phone
            WearLoadingState.TEST_CONNECTION -> R.string.keyscreen_loading_test_connection
            WearLoadingState.CONNECTING_FLIPPER -> R.string.keyscreen_loading_connecting_flipper
            WearLoadingState.INITIALIZING -> R.string.keyscreen_loading_initializing
        },
        iconId = null,
        picture = null,
        progress = null,
        progressColor = Color.Transparent
    )
}
