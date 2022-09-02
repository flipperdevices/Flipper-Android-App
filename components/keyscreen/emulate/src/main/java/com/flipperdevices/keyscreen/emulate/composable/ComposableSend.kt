package com.flipperdevices.keyscreen.emulate.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.ktx.onHoldPress
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.emulate.R
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableActionDisable
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableActionFlipper
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableActionLoading
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableAlreadyOpenedAppDialog
import com.flipperdevices.keyscreen.emulate.model.DisableButtonReason
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import com.flipperdevices.keyscreen.emulate.viewmodel.EmulateViewModel
import tangle.viewmodel.compose.tangleViewModel

private const val SEND_BUTTON_SCALE = 1.06f

@Composable
fun ComposableSend(modifier: Modifier = Modifier, flipperKey: FlipperKey) {
    val emulateViewModel = tangleViewModel<EmulateViewModel>()
    val emulateButtonState by emulateViewModel.getEmulateButtonStateFlow().collectAsState()

    if (!flipperKey.synchronized) {
        ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_send,
            iconId = DesignSystem.drawable.ic_send,
            reason = DisableButtonReason.NOT_SYNCHRONIZED
        )
        return
    }

    if (emulateButtonState == EmulateButtonState.AppAlreadyOpenDialog) {
        ComposableAlreadyOpenedAppDialog(emulateViewModel::closeDialog)
    }

    when (emulateButtonState) {
        is EmulateButtonState.Disabled -> {
            ComposableActionDisable(
                modifier = modifier,
                textId = R.string.keyscreen_send,
                iconId = DesignSystem.drawable.ic_send,
                reason = (emulateButtonState as EmulateButtonState.Disabled).reason
            )
        }
        is EmulateButtonState.Inactive,
        is EmulateButtonState.Active -> ComposableSendInternal(
            modifier = modifier,
            isAction = emulateButtonState is EmulateButtonState.Active,
            onTap = {
                emulateViewModel.onSinglePress(flipperKey)
            },
            onLongPressStart = {
                emulateViewModel.onStartEmulate(flipperKey)
            },
            onLongPressEnd = {
                emulateViewModel.onStopEmulate()
            }
        )
        is EmulateButtonState.Loading -> ComposableActionLoading(
            modifier = modifier,
            loadingState = (emulateButtonState as EmulateButtonState.Loading).state
        )
    }
}

@Composable
private fun ComposableSendInternal(
    modifier: Modifier = Modifier,
    isAction: Boolean,
    onTap: () -> Unit = {},
    onLongPressStart: () -> Unit = {},
    onLongPressEnd: () -> Unit = {}
) {
    val textId = if (isAction) R.string.keyscreen_sending else R.string.keyscreen_send
    val scale = animateFloatAsState(if (isAction) SEND_BUTTON_SCALE else 1f)

    val modifierAction = modifier
        .scale(scale.value)
        .onHoldPress(
            onTap = onTap,
            onLongPressStart = onLongPressStart,
            onLongPressEnd = onLongPressEnd
        )

    ComposableActionFlipper(
        modifier = modifierAction,
        color = LocalPallet.current.accent,
        textId = textId,
        iconId = DesignSystem.drawable.ic_send,
        animId = DesignSystem.raw.ic_sending,
        isAction = isAction
    ) {
        if (!isAction) {
            Text(
                text = stringResource(id = R.string.keyscreen_sending_desc),
                style = LocalTypography.current.subtitleM12,
                color = LocalPallet.current.text20,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableSendPreview() {
    FlipperThemeInternal {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            ComposableSendInternal(isAction = true)
        }
    }
}
