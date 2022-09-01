package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.emulate.R
import com.flipperdevices.keyscreen.emulate.model.DisableButtonReason

@Composable
fun ComposableActionDisable(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    @StringRes textId: Int,
    reason: DisableButtonReason
) {
    ComposableEmulateButton(
        modifier,
        buttonContent = {
            ComposableActionFlipperContent(
                iconId = iconId,
                textId = textId
            )
        },
        underButtonContent = {
            ComposableDisableButtonReason(reason)
        }
    )
}

@Composable
private fun ComposableDisableButtonReason(reason: DisableButtonReason) {
    val textId = when (reason) {
        DisableButtonReason.UNKNOWN -> {
            Spacer(modifier = Modifier.height(15.dp))
            return
        }
        DisableButtonReason.UPDATE_FLIPPER -> R.string.emulate_disabled_update_flipper
        DisableButtonReason.NOT_CONNECTED -> R.string.emulate_disabled_not_connected
        DisableButtonReason.NOT_SYNCHRONIZED -> R.string.emulate_disabled_not_synchronized
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier
                .size(14.dp)
                .padding(horizontal = 4.dp),
            painter = painterResource(DesignSystem.drawable.ic_warning),
            contentDescription = stringResource(textId),
            tint = LocalPallet.current.warningColor
        )
        Text(
            text = stringResource(textId),
            style = LocalTypography.current.bodyR14,
            color = LocalPallet.current.text30
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableComposableDisableActionPreview() {
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
