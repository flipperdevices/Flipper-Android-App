package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.flipperdevices.screenstreaming.impl.R

private val CONTROL_SIZE = 128.dp

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Preview(
    showSystemUi = true,
    showBackground = true
)
@Suppress("LongMethod")
@Composable
fun ComposableControlButtons(
    onPressButton: (ButtonEnum) -> Unit = {},
    onLongPressButton: (ButtonEnum) -> Unit = {}
) {
    /**
     * |----|  up  |-------|
     * |left|  ok  | right |
     * |----| down | back |
     */
    ConstraintLayout {
        val (left, right, up, down, ok, back) = createRefs()

        Icon(
            modifier = Modifier
                .constrainAs(up) {
                    top.linkTo(parent.top)
                    bottom.linkTo(ok.top)
                    start.linkTo(ok.start)
                    end.linkTo(ok.end)
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = { onPressButton(ButtonEnum.UP) },
                    onLongClick = { onLongPressButton(ButtonEnum.UP) }
                )
                .size(size = CONTROL_SIZE),
            painter = painterResource(R.drawable.ic_arrow_up),
            contentDescription = stringResource(R.string.control_up)
        )

        Icon(
            modifier = Modifier
                .constrainAs(left) {
                    start.linkTo(parent.start)
                    end.linkTo(ok.start)
                    top.linkTo(ok.top)
                    bottom.linkTo(ok.bottom)
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = { onPressButton(ButtonEnum.LEFT) },
                    onLongClick = { onLongPressButton(ButtonEnum.LEFT) }
                )
                .size(size = CONTROL_SIZE),
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = stringResource(R.string.control_left)
        )

        Icon(
            modifier = Modifier
                .constrainAs(ok) {
                    start.linkTo(left.end)
                    end.linkTo(right.start)
                    top.linkTo(up.bottom)
                    bottom.linkTo(down.top)
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = { onPressButton(ButtonEnum.OK) },
                    onLongClick = { onLongPressButton(ButtonEnum.OK) }
                )
                .size(size = CONTROL_SIZE),
            painter = painterResource(R.drawable.ic_circle),
            contentDescription = stringResource(R.string.control_ok)
        )

        Icon(
            modifier = Modifier
                .constrainAs(right) {
                    start.linkTo(ok.end)
                    end.linkTo(parent.end)
                    top.linkTo(ok.top)
                    bottom.linkTo(ok.bottom)
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = { onPressButton(ButtonEnum.RIGHT) },
                    onLongClick = { onLongPressButton(ButtonEnum.RIGHT) }
                )
                .size(size = CONTROL_SIZE),
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = stringResource(R.string.control_right)
        )

        Icon(
            modifier = Modifier
                .constrainAs(down) {
                    start.linkTo(ok.start)
                    end.linkTo(ok.end)
                    top.linkTo(ok.bottom)
                    bottom.linkTo(parent.bottom)
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = { onPressButton(ButtonEnum.DOWN) },
                    onLongClick = { onLongPressButton(ButtonEnum.DOWN) }
                )
                .size(size = CONTROL_SIZE),
            painter = painterResource(R.drawable.ic_arrow_down),
            contentDescription = stringResource(R.string.control_down)
        )

        Icon(
            modifier = Modifier
                .constrainAs(back) {
                    start.linkTo(right.start)
                    end.linkTo(right.end)
                    top.linkTo(down.top)
                    bottom.linkTo(down.bottom)
                }
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = { onPressButton(ButtonEnum.BACK) },
                    onLongClick = { onLongPressButton(ButtonEnum.BACK) }
                )
                .size(size = CONTROL_SIZE),
            painter = painterResource(R.drawable.ic_back),
            contentDescription = stringResource(R.string.control_back)
        )

        createVerticalChain(
            up, ok, down,
            chainStyle = ChainStyle.Packed
        )
        createHorizontalChain(
            left, ok, right,
            chainStyle = ChainStyle.Packed
        )
    }
}
