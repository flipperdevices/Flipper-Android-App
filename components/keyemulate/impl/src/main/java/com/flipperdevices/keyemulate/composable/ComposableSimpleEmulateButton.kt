package com.flipperdevices.keyemulate.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.image.Picture
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.composable.common.ComposableActionDisable
import com.flipperdevices.keyemulate.composable.common.ComposableActionLoading
import com.flipperdevices.keyemulate.composable.common.ComposableEmulateButtonWithText
import com.flipperdevices.keyemulate.composable.common.ComposableErrorDialogs
import com.flipperdevices.keyemulate.impl.R
import com.flipperdevices.keyemulate.model.DisableButtonReason
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.viewmodel.SimpleEmulateViewModel
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableSimpleEmulateButton(
    emulateConfig: EmulateConfig,
    isSynchronized: Boolean,
    modifier: Modifier = Modifier
) {
    val emulateViewModel = tangleViewModel<SimpleEmulateViewModel>()
    val emulateButtonState by emulateViewModel.getEmulateButtonStateFlow().collectAsState()

    val buttonActiveModifier = Modifier.clickableRipple {
        if (emulateButtonState is EmulateButtonState.Inactive) {
            emulateViewModel.onStartEmulate(emulateConfig)
        } else if (emulateButtonState is EmulateButtonState.Active) {
            emulateViewModel.onStopEmulate()
        }
    }

    if (!isSynchronized) {
        ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_emulate,
            iconId = DesignSystem.drawable.ic_emulate,
            reason = DisableButtonReason.NOT_SYNCHRONIZED
        )
        return
    }

    ComposableErrorDialogs(emulateButtonState, emulateViewModel::closeDialog)

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
