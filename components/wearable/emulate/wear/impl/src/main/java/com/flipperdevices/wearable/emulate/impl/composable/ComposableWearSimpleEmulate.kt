package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.api.EmulateProgress
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import com.flipperdevices.wearable.emulate.impl.R
import rememberRipple

@Composable
fun ComposableWearSimpleEmulate(
    modifier: Modifier,
    emulateProgress: EmulateProgress?,
    keyEmulateUiApi: KeyEmulateUiApi,
    onClickEmulate: () -> Unit,
    onStopEmulate: () -> Unit
) {
    if (emulateProgress == null) {
        keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = modifier,
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
    } else {
        keyEmulateUiApi.ComposableEmulateButtonRaw(
            modifier = modifier,
            buttonContentModifier = Modifier.clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(),
                onClick = onStopEmulate
            ),
            emulateProgress = emulateProgress,
            picture = null,
            textId = R.string.keyscreen_emulating,
            color = LocalPallet.current.actionOnFlipperProgress,
            progressColor = LocalPallet.current.actionOnFlipperEnable
        )
    }
}
