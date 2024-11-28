package com.flipperdevices.keyemulate.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.ktx.onScrollHoldPress
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.composable.common.ComposableActionDisable
import com.flipperdevices.keyemulate.composable.common.ComposableActionLoading
import com.flipperdevices.keyemulate.composable.common.ComposableErrorDialogs
import com.flipperdevices.keyemulate.composable.common.InternalComposableEmulateButtonWithText
import com.flipperdevices.keyemulate.model.EmulateButtonState
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.keyemulate.viewmodel.InfraredViewModel
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig

@Composable
internal fun ComposableInfraredSendButton(
    emulateConfig: EmulateConfig,
    isSynchronized: Boolean,
    emulateViewModel: InfraredViewModel,
    modifier: Modifier = Modifier,
) {
    val rootNavigation = LocalRootNavigation.current
    val name = emulateConfig.args ?: return

    val emulateButtonState by emulateViewModel.getEmulateButtonStateFlow().collectAsState()

    if (!isSynchronized) {
        ComposableActionDisable(
            modifier = modifier,
            text = name,
            iconId = null,
            reason = null
        )
        return
    }

    ComposableErrorDialogs(emulateButtonState, emulateViewModel::closeDialog) {
        rootNavigation.push(RootScreenConfig.ScreenStreaming)
    }

    when (emulateButtonState) {
        is EmulateButtonState.Disabled -> ComposableActionDisable(
            modifier = modifier,
            text = name,
            iconId = null,
            reason = null
        )

        is EmulateButtonState.Active,
        is EmulateButtonState.Inactive ->
            ComposableActiveStateEmulateInternal(
                modifier = modifier,
                emulateButtonState = emulateButtonState,
                emulateViewModel = emulateViewModel,
                emulateConfig = emulateConfig
            )

        is EmulateButtonState.Loading -> ComposableActionLoading(
            modifier = modifier,
            loadingState = null
        )
    }
}

@Composable
private fun ComposableActiveStateEmulateInternal(
    emulateViewModel: InfraredViewModel,
    emulateConfig: EmulateConfig,
    emulateButtonState: EmulateButtonState,
    modifier: Modifier = Modifier
) {
    val name = emulateConfig.args ?: return

    val buttonActiveModifier = Modifier.onScrollHoldPress(
        onTap = {
            emulateViewModel.onSinglePress(emulateConfig.copy(isPressRelease = true))
        },
        onLongPressStart = {
            emulateViewModel.onStartEmulate(emulateConfig)
        },
        onLongPressEnd = {
            emulateViewModel.onStopEmulate()
        }
    )

    val isActive = when {
        emulateButtonState !is EmulateButtonState.Active -> false
        emulateButtonState.config == emulateConfig -> true
        else -> false
    }

    val emulateProgress = if (isActive) {
        (emulateButtonState as? EmulateButtonState.Active)?.progress
    } else {
        null
    }

    ComposableActiveEmulateInternal(
        modifier = modifier,
        buttonActiveModifier = buttonActiveModifier,
        emulateProgress = emulateProgress,
        isActive = isActive,
        text = name
    )
}

/**
 * We need to use a single composable function here because
 * the gesture listener must be the same for both components and the components must be the same
 */
@Composable
private fun ComposableActiveEmulateInternal(
    emulateProgress: EmulateProgress?,
    isActive: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    buttonActiveModifier: Modifier = Modifier
) {
    val color = if (isActive) {
        LocalPallet.current.actionOnFlipperInfraredProgress
    } else {
        LocalPallet.current.actionOnFlipperInfraredEnable
    }
    val progressColor = if (isActive) {
        LocalPallet.current.actionOnFlipperInfraredEnable
    } else {
        Color.Transparent
    }

    InternalComposableEmulateButtonWithText(
        modifier = modifier,
        buttonModifier = buttonActiveModifier,
        buttonText = text,
        color = color,
        progressColor = progressColor,
        progress = emulateProgress,
        picture = null
    )
}
