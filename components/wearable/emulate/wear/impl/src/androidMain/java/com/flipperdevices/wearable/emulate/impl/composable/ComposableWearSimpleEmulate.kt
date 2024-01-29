package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.wearable.emulate.impl.R

@Composable
fun ComposableWearSimpleEmulate(
    emulateProgress: EmulateProgress?,
    keyEmulateUiApi: KeyEmulateUiApi,
    onClickEmulate: () -> Unit,
    modifier: Modifier = Modifier,
    onStopEmulate: () -> Unit
) {
    if (emulateProgress == null) {
        keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = modifier,
            buttonContentModifier = Modifier.clickableRipple(onClick = onClickEmulate),
            picture = null,
            textId = R.string.keyscreen_emulate,
            color = LocalPallet.current.actionOnFlipperEnable,
            progressColor = Color.Transparent,
            emulateProgress = null
        )
    } else {
        keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = modifier,
            buttonContentModifier = Modifier.clickableRipple(onClick = onStopEmulate),
            emulateProgress = emulateProgress,
            picture = null,
            textId = R.string.keyscreen_emulating,
            color = LocalPallet.current.actionOnFlipperProgress,
            progressColor = LocalPallet.current.actionOnFlipperEnable
        )
    }
}
