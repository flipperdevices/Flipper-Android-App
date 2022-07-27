package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.ui.ktx.onHoldPress
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.model.FlipperDeviceState
import com.flipperdevices.keyscreen.impl.viewmodel.FlipperDeviceViewModel

@Composable
fun ComposableSend(
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit = {}
) {
    val flipperDeviceViewModel = viewModel<FlipperDeviceViewModel>()
    val flipperDeviceState by flipperDeviceViewModel.getFlipperDeviceState().collectAsState()
    val enabled = flipperDeviceState == FlipperDeviceState.CONNECTED

    if (enabled) {
        ComposableActionDisable(
            textId = R.string.keyscreen_send,
            iconId = DesignSystem.drawable.ic_send
        )
    } else {
        ComposableSendInternal(
            modifier = modifier,
            onClick = onClick
        )
    }
}

@Composable
private fun ComposableSendInternal(
    modifier: Modifier = Modifier,
    onClick: ((Boolean) -> Unit) = {}
) {
    var isAction by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (isAction) 1.06f else 1f)

    val modifierAction = modifier
        .scale(scale.value)
        .onHoldPress(
            onLongPressStart = {
                isAction = true
                onClick.invoke(isAction)
            },
            onLongPressEnd = {
                isAction = false
                onClick.invoke(isAction)
            }
        )

    ComposableActionFlipper(
        modifier = modifierAction,
        color = LocalPallet.current.accent,
        textId = R.string.keyscreen_send,
        iconId = DesignSystem.drawable.ic_send,
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
        Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxSize()) {
            ComposableSendInternal()
        }
    }
}
