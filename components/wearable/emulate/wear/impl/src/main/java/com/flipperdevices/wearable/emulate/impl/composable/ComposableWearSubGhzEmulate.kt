package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.onHoldPress
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.api.EmulateProgress
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import com.flipperdevices.keyscreen.api.LoadingState
import com.flipperdevices.wearable.emulate.impl.R
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState

@Composable
fun ComposableWearSubGhzEmulate(
    state: WearEmulateState,
    keyEmulateUiApi: KeyEmulateUiApi,
    onClickEmulate: () -> Unit,
    onSinglePress: () -> Unit,
    onStopEmulate: () -> Unit
) {
    val buttonPadding = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
    val buttonActiveModifier = Modifier.onHoldPress(
        onTap = onSinglePress,
        onLongPressStart = onClickEmulate,
        onLongPressEnd = onStopEmulate
    )

    when (state) {
        is WearEmulateState.Loading -> keyEmulateUiApi.ComposableEmulateLoading(
            modifier = buttonPadding,
            loadingState = LoadingState.CONNECTING
        )
        is WearEmulateState.Emulating -> keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = buttonPadding,
            buttonContentModifier = buttonActiveModifier,
            emulateProgress = EmulateProgress.Infinite,
            picture = null,
            textId = R.string.keyscreen_sending,
            color = LocalPallet.current.actionOnFlipperSubGhzProgress,
            progressColor = LocalPallet.current.actionOnFlipperSubGhzEnable
        )
        is WearEmulateState.FoundNode -> keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = buttonPadding,
            buttonContentModifier = buttonActiveModifier,
            picture = null,
            textId = R.string.keyscreen_send,
            color = LocalPallet.current.actionOnFlipperSubGhzEnable,
            progressColor = Color.Transparent,
            emulateProgress = null
        )
        is WearEmulateState.NotFoundNode -> {}
    }
}
