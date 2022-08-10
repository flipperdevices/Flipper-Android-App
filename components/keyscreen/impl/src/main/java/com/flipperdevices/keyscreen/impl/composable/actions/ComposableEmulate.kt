package com.flipperdevices.keyscreen.impl.composable.actions

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
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.common.ComposableActionDisable
import com.flipperdevices.keyscreen.impl.composable.actions.common.ComposableActionFlipper
import com.flipperdevices.keyscreen.impl.model.EmulateButtonState
import com.flipperdevices.keyscreen.impl.viewmodel.EmulateViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableEmulate(modifier: Modifier = Modifier, flipperKey: FlipperKey) {
    val emulateViewModel = tangleViewModel<EmulateViewModel>()
    val emulateButtonState by emulateViewModel.getEmulateButtonStateFlow().collectAsState()
    if (!flipperKey.synchronized) {
        ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_emulate,
            iconId = DesignSystem.drawable.ic_emulate
        )
        return
    }

    when (emulateButtonState) {
        EmulateButtonState.DISABLED -> ComposableActionDisable(
            modifier = modifier,
            textId = R.string.keyscreen_emulate,
            iconId = DesignSystem.drawable.ic_emulate
        )
        EmulateButtonState.INACTIVE -> ComposableEmulateInternal(
            modifier = modifier,
            isAction = false,
            onClick = {
                emulateViewModel.onStartEmulate(flipperKey)
            }
        )
        EmulateButtonState.ACTIVE -> ComposableEmulateInternal(
            modifier = modifier,
            isAction = true,
            onClick = {
                emulateViewModel.onStopEmulate()
            }
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
        animId = DesignSystem.raw.ic_emulate,
        isAction = isAction
    ) {
        if (isAction) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
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
