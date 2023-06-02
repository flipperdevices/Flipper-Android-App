package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.ktx.onHoldPress
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.wearable.emulate.impl.R

@Composable
fun ComposableWearSubGhzEmulate(
    emulateProgress: EmulateProgress?,
    keyEmulateUiApi: KeyEmulateUiApi,
    onClickEmulate: () -> Unit,
    onSinglePress: () -> Unit,
    modifier: Modifier = Modifier,
    onStopEmulate: () -> Unit
) {
    val buttonActiveModifier = Modifier.onHoldPress(
        onTap = onSinglePress,
        onLongPressStart = onClickEmulate,
        onLongPressEnd = onStopEmulate
    )

    if (emulateProgress == null) {
        keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = modifier,
            buttonContentModifier = buttonActiveModifier,
            picture = null,
            textId = R.string.keyscreen_send,
            color = LocalPallet.current.actionOnFlipperSubGhzEnable,
            progressColor = Color.Transparent,
            emulateProgress = null
        )
    } else {
        keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = modifier,
            buttonContentModifier = buttonActiveModifier,
            emulateProgress = emulateProgress,
            picture = null,
            textId = R.string.keyscreen_sending,
            color = LocalPallet.current.actionOnFlipperSubGhzProgress,
            progressColor = LocalPallet.current.actionOnFlipperSubGhzEnable
        )
    }
}
