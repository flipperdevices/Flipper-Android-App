package com.flipperdevices.keyscreen.impl.composable.actions

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
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.common.ComposableActionDisable
import com.flipperdevices.keyscreen.impl.composable.actions.common.ComposableActionFlipper
import com.flipperdevices.keyscreen.impl.model.EmulateButtonState
import com.flipperdevices.keyscreen.impl.viewmodel.EmulateViewModel
import tangle.viewmodel.compose.tangleViewModel

private const val SEND_BUTTON_SCALE = 1.06f

@Composable
fun ComposableSend(modifier: Modifier = Modifier, flipperKey: FlipperKey) {
    val flipperDeviceViewModel = tangleViewModel<EmulateViewModel>()
    val emulateButtonState by flipperDeviceViewModel.getEmulateButtonStateFlow().collectAsState()

    if (!flipperKey.synchronized) {
        ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_send,
            iconId = DesignSystem.drawable.ic_send
        )
        return
    }

    when (emulateButtonState) {
        EmulateButtonState.DISABLED -> ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_send,
            iconId = DesignSystem.drawable.ic_send
        )
        EmulateButtonState.INACTIVE,
        EmulateButtonState.ACTIVE -> ComposableSendInternal(
            modifier = modifier,
            isAction = emulateButtonState == EmulateButtonState.ACTIVE,
            onTap = {
                flipperDeviceViewModel.onSinglePress(flipperKey)
            },
            onLongPressStart = {
                flipperDeviceViewModel.onStartEmulate(flipperKey)
            },
            onLongPressEnd = {
                flipperDeviceViewModel.onStopEmulate()
            }
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
        animId = DesignSystem.raw.ic_send,
        isAction = isAction
    ) {
        if (!isAction) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
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
