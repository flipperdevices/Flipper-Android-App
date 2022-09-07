package com.flipperdevices.keyscreen.emulate.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.core.ui.ktx.onHoldPress
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.emulate.R
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableActionDisable
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableActionLoading
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableAlreadyOpenedAppDialog
import com.flipperdevices.keyscreen.emulate.composable.common.ComposableEmulateButtonWithText
import com.flipperdevices.keyscreen.emulate.model.DisableButtonReason
import com.flipperdevices.keyscreen.emulate.model.EmulateButtonState
import com.flipperdevices.keyscreen.emulate.model.EmulateProgress
import com.flipperdevices.keyscreen.emulate.model.Picture
import com.flipperdevices.keyscreen.emulate.viewmodel.EmulateViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableSubGhzSendButton(modifier: Modifier = Modifier, flipperKey: FlipperKey) {
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
        is EmulateButtonState.Disabled -> ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_send,
            iconId = DesignSystem.drawable.ic_send,
            reason = (emulateButtonState as EmulateButtonState.Disabled).reason
        )
        is EmulateButtonState.Active,
        is EmulateButtonState.Inactive -> {
            ComposableActiveStateEmulateInternal(
                modifier = modifier,
                emulateButtonState = emulateButtonState,
                emulateViewModel = emulateViewModel,
                flipperKey = flipperKey
            )
        }
        is EmulateButtonState.Loading -> ComposableActionLoading(
            modifier = modifier,
            loadingState = (emulateButtonState as EmulateButtonState.Loading).state
        )
    }
}

@Composable
private fun ComposableActiveStateEmulateInternal(
    modifier: Modifier,
    emulateViewModel: EmulateViewModel,
    flipperKey: FlipperKey,
    emulateButtonState: EmulateButtonState
) {
    val buttonActiveModifier = Modifier.onHoldPress(
        onTap = {
            if (emulateButtonState is EmulateButtonState.Inactive) {
                emulateViewModel.onSinglePress(flipperKey)
            } else if (emulateButtonState is EmulateButtonState.Active) {
                emulateViewModel.onStopEmulate()
            }
        },
        onLongPressStart = {
            if (emulateButtonState is EmulateButtonState.Inactive) {
                emulateViewModel.onStartEmulate(flipperKey)
            }
        },
        onLongPressEnd = {
            if (emulateButtonState is EmulateButtonState.Active) {
                emulateViewModel.onStopEmulate()
            }
        }
    )
    ComposableActiveEmulateInternal(
        modifier = modifier,
        buttonActiveModifier = buttonActiveModifier,
        emulateProgress = (emulateButtonState as? EmulateButtonState.Active)?.progress,
        isActive = emulateButtonState is EmulateButtonState.Active
    )
}

/**
 * We need to use a single composable function here because
 * the gesture listener must be the same for both components and the components must be the same
 */
@Composable
private fun ComposableActiveEmulateInternal(
    modifier: Modifier,
    buttonActiveModifier: Modifier,
    emulateProgress: EmulateProgress?,
    isActive: Boolean
) {
    val textId = if (isActive) R.string.keyscreen_sending else R.string.keyscreen_send
    val color = if (isActive) {
        LocalPallet.current.actionOnFlipperSubGhzProgress
    } else LocalPallet.current.actionOnFlipperSubGhzEnable
    val progressColor = if (isActive) {
        LocalPallet.current.actionOnFlipperSubGhzEnable
    } else Color.Transparent
    val descriptionId = if (isActive) null else R.string.keyscreen_sending_desc
    val picture = if (isActive) Picture.LottieRes(
        DesignSystem.raw.ic_sending,
        DesignSystem.drawable.ic_send
    ) else Picture.StaticRes(DesignSystem.drawable.ic_send)

    ComposableEmulateButtonWithText(
        modifier = modifier,
        buttonModifier = buttonActiveModifier,
        buttonTextId = textId,
        textId = descriptionId,
        color = color,
        progressColor = progressColor,
        progress = emulateProgress,
        picture = picture
    )
}
