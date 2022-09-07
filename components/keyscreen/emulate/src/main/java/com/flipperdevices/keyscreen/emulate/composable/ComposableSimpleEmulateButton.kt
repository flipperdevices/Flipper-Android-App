package com.flipperdevices.keyscreen.emulate.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.emulate.R
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableActionDisable
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableActionLoading
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableAlreadyOpenedAppDialog
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableEmulateButtonWithText
import com.flipperdevices.keyscreen.emulate.model.DisableButtonReason
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import com.flipperdevices.keyscreen.emulate.model.Picture
import com.flipperdevices.keyscreen.emulate.viewmodel.SimpleEmulateViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableSimpleEmulateButton(modifier: Modifier = Modifier, flipperKey: FlipperKey) {
    val emulateViewModel = tangleViewModel<SimpleEmulateViewModel>()
    val emulateButtonState by emulateViewModel.getEmulateButtonStateFlow().collectAsState()

    val buttonActiveModifier = Modifier.clickable(
        interactionSource = MutableInteractionSource(),
        indication = rememberRipple(),
        onClick = {
            if (emulateButtonState is EmulateButtonState.Inactive) {
                emulateViewModel.onStartEmulate(flipperKey)
            } else if (emulateButtonState is EmulateButtonState.Active) {
                emulateViewModel.onStopEmulate()
            }
        }
    )

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
        is EmulateButtonState.Active -> ComposableEmulateButtonWithText(
            modifier = modifier,
            buttonModifier = buttonActiveModifier,
            buttonTextId = R.string.keyscreen_emulating,
            color = LocalPallet.current.actionOnFlipperProgress,
            progressColor = LocalPallet.current.actionOnFlipperEnable,
            progress = (emulateButtonState as EmulateButtonState.Active).progress,
            picture = Picture.LottieRes(
                DesignSystem.raw.ic_emulating,
                DesignSystem.drawable.ic_emulate
            ),
            textId = R.string.keyscreen_emulating_desc
        )
        is EmulateButtonState.Inactive -> ComposableEmulateButtonWithText(
            modifier = modifier,
            buttonTextId = R.string.keyscreen_emulate,
            buttonModifier = buttonActiveModifier,
            color = LocalPallet.current.actionOnFlipperEnable,
            picture = Picture.StaticRes(DesignSystem.drawable.ic_emulate)
        )
        is EmulateButtonState.Loading -> ComposableActionLoading(
            modifier = modifier,
            loadingState = (emulateButtonState as EmulateButtonState.Loading).state
        )
    }
}
