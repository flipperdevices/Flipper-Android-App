package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.wearable.emulate.impl.R

@Composable
fun ComposableActionDisable(
    keyEmulateUiApi: KeyEmulateUiApi,
    keyType: FlipperKeyType?,
    modifier: Modifier = Modifier
) {
    val textId = when (keyType) {
        FlipperKeyType.SUB_GHZ,
        FlipperKeyType.INFRARED -> R.string.keyscreen_emulate
        null,
        FlipperKeyType.RFID,
        FlipperKeyType.NFC,
        FlipperKeyType.I_BUTTON -> R.string.keyscreen_send
    }

    keyEmulateUiApi.ComposableEmulateButtonWithText(
        modifier = modifier,
        progress = null,
        buttonTextId = textId,
        picture = null,
        color = LocalPallet.current.text8,
        textId = R.string.keyscreen_not_supported,
        iconId = com.flipperdevices.core.ui.res.R.drawable.ic_warning,
        buttonModifier = Modifier,
        progressColor = Color.Transparent
    )
}
