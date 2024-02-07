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
import com.flipperdevices.keyemulate.composable.common.ComposableErrorDialogs
import com.flipperdevices.keyemulate.composable.common.InternalComposableEmulateButtonWithText
import com.flipperdevices.keyemulate.impl.R
import com.flipperdevices.keyemulate.model.DisableButtonReason
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.viewmodel.SimpleEmulateViewModel
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig

@Composable
fun ComposableSimpleEmulateButton(
    emulateConfig: EmulateConfig,
    isSynchronized: Boolean,
    emulateViewModel: SimpleEmulateViewModel,
    modifier: Modifier = Modifier
) {
    val rootNavigation = LocalRootNavigation.current
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
            iconId = R.drawable.ic_emulate,
            reason = DisableButtonReason.NOT_SYNCHRONIZED
        )
        return
    }

    ComposableErrorDialogs(emulateButtonState, emulateViewModel::closeDialog) {
        rootNavigation.push(RootScreenConfig.ScreenStreaming)
    }

    when (emulateButtonState) {
        is EmulateButtonState.Disabled -> ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_emulate,
            iconId = R.drawable.ic_emulate,
            reason = (emulateButtonState as EmulateButtonState.Disabled).reason
        )

        is EmulateButtonState.Active -> InternalComposableEmulateButtonWithText(
            modifier = modifier,
            buttonModifier = buttonActiveModifier,
            buttonTextId = R.string.keyscreen_emulating,
            color = LocalPallet.current.actionOnFlipperProgress,
            progressColor = LocalPallet.current.actionOnFlipperEnable,
            progress = (emulateButtonState as EmulateButtonState.Active).progress,
            picture = Picture.LottieRes(
                R.raw.ic_emulating,
                R.drawable.ic_emulate
            ),
            textId = R.string.keyscreen_emulating_desc
        )

        is EmulateButtonState.Inactive -> InternalComposableEmulateButtonWithText(
            modifier = modifier,
            buttonTextId = R.string.keyscreen_emulate,
            buttonModifier = buttonActiveModifier,
            color = LocalPallet.current.actionOnFlipperEnable,
            picture = Picture.StaticRes(R.drawable.ic_emulate)
        )

        is EmulateButtonState.Loading -> ComposableActionLoading(
            modifier = modifier,
            loadingState = (emulateButtonState as EmulateButtonState.Loading).state
        )
    }
}
