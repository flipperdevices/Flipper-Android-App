package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

private const val BUTTON_WEIGHT = 0.3f

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Suppress("LongMethod")
@Composable
fun ComposableControlButtons(
    modifier: Modifier = Modifier,
    onPressButton: (ButtonEnum) -> Unit = {},
    onLongPressButton: (ButtonEnum) -> Unit = {}
) {
    /**
     * | photo |  up  | unlock |
     * | left |  ok  | right |
     * |------| down  | back |
     */
    Column(modifier) {
        ControlRow(
            start = ButtonEnum.SCREENSHOT,
            center = ButtonEnum.UP,
            end = ButtonEnum.UNLOCK,
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
            end = ButtonEnum.BACK,
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

    Icon(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { onPressButton(button) },
                onLongClick = { onLongPressButton(button) }
            ),
        painter = painterResource(button.icon),
        contentDescription = stringResource(button.description)
    )
}
