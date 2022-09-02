package com.flipperdevices.keyscreen.emulate.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.dao.api.model.FlipperKey
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

@Composable
fun ComposableEmulate(modifier: Modifier = Modifier, flipperKey: FlipperKey) {
    val emulateViewModel = tangleViewModel<EmulateViewModel>()
    val emulateButtonState by emulateViewModel.getEmulateButtonStateFlow().collectAsState()
    if (!flipperKey.synchronized) {
        ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_emulate,
            iconId = DesignSystem.drawable.ic_emulate,
            reason = DisableButtonReason.NOT_SYNCHRONIZED
        )
        return
    }

    if (emulateButtonState == EmulateButtonState.AppAlreadyOpenDialog) {
        ComposableAlreadyOpenedAppDialog(emulateViewModel::closeDialog)
    }

    when (emulateButtonState) {
        is EmulateButtonState.Disabled -> ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_emulate,
            iconId = DesignSystem.drawable.ic_emulate,
            reason = (emulateButtonState as EmulateButtonState.Disabled).reason
        )
        is EmulateButtonState.Active -> ComposableEmulateInternal(
            modifier = modifier,
            isAction = true,
            onClick = emulateViewModel::onStopEmulate
        )
        is EmulateButtonState.Inactive -> ComposableEmulateInternal(
            modifier = modifier,
            isAction = false,
            onClick = {
                emulateViewModel.onStartEmulate(flipperKey)
            }
        )
        is EmulateButtonState.Loading -> ComposableActionLoading(
            modifier = modifier,
            loadingState = (emulateButtonState as EmulateButtonState.Loading).state
        )
    }
}

@Composable
private fun ComposableEmulateInternal(
    modifier: Modifier = Modifier,
    isAction: Boolean,
    onClick: (() -> Unit) = {}
) {
    val textId = if (isAction) R.string.keyscreen_emulating else R.string.keyscreen_emulate

    val modifierAction = modifier.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )

    ComposableActionFlipper(
        modifier = modifierAction,
        color = LocalPallet.current.accentSecond,
        textId = textId,
        iconId = DesignSystem.drawable.ic_emulate,
        animId = DesignSystem.raw.ic_emulating,
        isAction = isAction
    ) {
        if (isAction) {
            Text(
                text = stringResource(id = R.string.keyscreen_emulating_desc),
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
private fun ComposableEmulatePreview() {
    FlipperThemeInternal {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            ComposableEmulateInternal(isAction = true)
        }
    }
}
