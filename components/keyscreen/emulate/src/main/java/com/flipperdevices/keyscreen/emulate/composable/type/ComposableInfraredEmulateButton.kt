package com.flipperdevices.keyscreen.emulate.composable.type

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableErrorDialogs
import com.flipperdevices.keyscreen.emulate.composable.common.action.ComposableActionDisable
import com.flipperdevices.keyscreen.emulate.composable.common.action.ComposableActionLoading
import com.flipperdevices.keyscreen.emulate.composable.common.action.ComposableEmulateButtonWithText
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import com.flipperdevices.keyscreen.emulate.viewmodel.type.InfraredViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableInfraredEmulateButton(
    flipperKey: FlipperKey,
    name: String,
    modifier: Modifier = Modifier
) {
    val emulateViewModel = tangleViewModel<InfraredViewModel>()
    val emulateButtonState by emulateViewModel.getEmulateButtonStateFlow().collectAsState()

    if (!flipperKey.synchronized) {
        ComposableActionDisable(
            modifier = modifier,
            text = name,
            iconId = null
        )
        return
    }

    val buttonActiveModifier = Modifier.clickableRipple {
        if (emulateButtonState is EmulateButtonState.Inactive) {
            emulateViewModel.onSinglePress(flipperKey, name)
        } else if (emulateButtonState is EmulateButtonState.Active) {
            emulateViewModel.onStopEmulate()
        }
    }

    ComposableErrorDialogs(emulateButtonState, emulateViewModel::closeDialog)

    when (val localState = emulateButtonState) {
        is EmulateButtonState.Disabled -> ComposableActionDisable(
            modifier = modifier,
            text = name,
            iconId = null
        )
        is EmulateButtonState.Active -> ComposableEmulateButtonWithText(
            modifier = modifier,
            buttonModifier = buttonActiveModifier,
            buttonText = name,
            color = LocalPallet.current.actionOnFlipperInfraredProgress,
            progressColor = LocalPallet.current.actionOnFlipperInfraredEnable,
            progress = localState.progress,
            picture = null,
            textId = null
        )
        is EmulateButtonState.Inactive -> ComposableEmulateButtonWithText(
            modifier = modifier,
            buttonText = name,
            buttonModifier = buttonActiveModifier,
            color = LocalPallet.current.actionOnFlipperInfraredEnable,
            picture = null
        )
        is EmulateButtonState.Loading -> ComposableActionLoading(
            modifier = modifier,
            loadingState = localState.state
        )
    }
}
