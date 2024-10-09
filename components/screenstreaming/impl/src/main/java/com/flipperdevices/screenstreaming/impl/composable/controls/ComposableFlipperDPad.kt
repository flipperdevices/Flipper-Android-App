package com.flipperdevices.screenstreaming.impl.composable.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.screenstreaming.impl.composable.ButtonEnum

private const val BUTTON_WEIGHT = 0.3f

@Suppress("LongMethod")
@Composable
fun ComposableFlipperDPad(
    modifier: Modifier = Modifier,
    onPressButton: (ButtonEnum) -> Unit = {},
    onLongPressButton: (ButtonEnum) -> Unit = {}
) {
    /**
     * |------|  up  |------|
     * | left |  ok  | right |
     * |------| down  |-----|
     */
    Column(
        modifier
            .size(162.dp)
            .border(
                width = 3.dp,
                color = LocalPallet.current.screenStreamingBorderColor,
                shape = CircleShape
            )
            .padding(3.dp)
            .clip(CircleShape)
            .background(LocalPallet.current.accent)
    ) {
        ControlRow(
            start = null,
            center = ButtonEnum.UP,
            end = null,
            onPressButton,
            onLongPressButton
        )
        ControlRow(
            start = ButtonEnum.LEFT,
            center = ButtonEnum.OK,
            end = ButtonEnum.RIGHT,
            onPressButton,
            onLongPressButton
        )
        ControlRow(
            start = null,
            center = ButtonEnum.DOWN,
            end = null,
            onPressButton,
            onLongPressButton
        )
    }
}

@Composable
private fun ColumnScope.ControlRow(
    start: ButtonEnum?,
    center: ButtonEnum?,
    end: ButtonEnum?,
    onPressButton: (ButtonEnum) -> Unit,
    onLongPressButton: (ButtonEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .weight(BUTTON_WEIGHT)
            .fillMaxWidth()
    ) {
        ControlButton(
            modifier = Modifier.weight(BUTTON_WEIGHT),
            button = start,
            onPressButton = onPressButton,
            onLongPressButton = onLongPressButton
        )
        ControlButton(
            modifier = Modifier.weight(BUTTON_WEIGHT),
            button = center,
            onPressButton = onPressButton,
            onLongPressButton = onLongPressButton
        )
        ControlButton(
            modifier = Modifier.weight(BUTTON_WEIGHT),
            button = end,
            onPressButton = onPressButton,
            onLongPressButton = onLongPressButton
        )
    }
}

@Composable
@Suppress("ModifierReused")
private fun ControlButton(
    button: ButtonEnum?,
    onPressButton: (ButtonEnum) -> Unit,
    onLongPressButton: (ButtonEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (button == null) {
        Box(modifier)
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { onPressButton(button) },
                onLongClick = { onLongPressButton(button) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(44.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(button.icon),
                contentDescription = stringResource(button.description)
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableFlipperDPadPreview() {
    FlipperThemeInternal {
        ComposableFlipperDPad()
    }
}
