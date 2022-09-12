package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.api.EmulateProgress
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import com.flipperdevices.keyscreen.api.LoadingState
import com.flipperdevices.wearable.emulate.impl.R
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import rememberRipple

@Composable
fun ComposableWearSimpleEmulate(
    state: WearEmulateState,
    keyEmulateUiApi: KeyEmulateUiApi,
    onClickEmulate: () -> Unit
) {
    val buttonPadding = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
    when (state) {
        is WearEmulateState.Loading -> keyEmulateUiApi.ComposableEmulateLoading(
            modifier = buttonPadding,
            loadingState = LoadingState.CONNECTING
        )
        is WearEmulateState.Emulating -> keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = buttonPadding,
            buttonContentModifier = Modifier,
            emulateProgress = EmulateProgress.Infinite,
            picture = null,
            textId = R.string.keyscreen_emulating,
            color = LocalPallet.current.actionOnFlipperProgress,
            progressColor = LocalPallet.current.actionOnFlipperEnable
        )
        is WearEmulateState.FoundNode -> keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = buttonPadding,
            buttonContentModifier = Modifier.clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(),
                onClick = onClickEmulate
            ),
            picture = null,
            textId = R.string.keyscreen_emulate,
            color = LocalPallet.current.actionOnFlipperEnable,
            progressColor = Color.Transparent,
            emulateProgress = null
        )
        is WearEmulateState.NotFoundNode -> {}
    }
}
