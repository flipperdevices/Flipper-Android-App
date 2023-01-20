package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.api.Picture
import com.flipperdevices.keyscreen.emulate.R
import com.flipperdevices.keyscreen.emulate.model.DisableButtonReason
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableActionDisable(
    @DrawableRes iconId: Int,
    @StringRes textId: Int,
    reason: DisableButtonReason,
    modifier: Modifier = Modifier
) {
    val warningTextId = when (reason) {
        DisableButtonReason.UNKNOWN -> null
        DisableButtonReason.UPDATE_FLIPPER -> R.string.emulate_disabled_update_flipper
        DisableButtonReason.NOT_CONNECTED -> R.string.emulate_disabled_not_connected
        DisableButtonReason.NOT_SYNCHRONIZED -> R.string.emulate_disabled_not_synchronized
    }
    ComposableEmulateButtonWithText(
        modifier = modifier,
        progress = null,
        buttonTextId = textId,
        picture = Picture.StaticRes(iconId),
        color = LocalPallet.current.text8,
        textId = warningTextId,
        iconId = if (warningTextId != null) {
            DesignSystem.drawable.ic_warning
        } else {
            null
        }
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableDisableActionPreview() {
    FlipperThemeInternal {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            ComposableActionDisable(
                textId = R.string.keyscreen_emulate,
                iconId = DesignSystem.drawable.ic_emulate,
                reason = DisableButtonReason.NOT_CONNECTED
            )
            ComposableActionDisable(
                textId = R.string.keyscreen_send,
                iconId = DesignSystem.drawable.ic_send,
                reason = DisableButtonReason.UPDATE_FLIPPER
            )
        }
    }
}
