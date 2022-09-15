package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyscreen.emulate.R

private const val RADIUS_SLICE_UNDER_BUBBLE = 15f
private const val PADDING_END_SLICE_FROM_BUBBLE = 12
private const val SIZE_BUBBLE = 10
private const val DISTANCE_FROM_BUBBLE_BUTTON = 30
private const val PADDING_END_BUBBLE_FROM_DEVICE = 24
private const val DELAY_ANIMATION = 2000

@Composable
fun ComposableBubbleHoldToSend(positionYEmulateButton: Int) {
    var positionBubble by remember { mutableStateOf(IntSize.Zero) }
    var visibleState by remember { mutableStateOf(false) }
    Popup(
        alignment = Alignment.TopEnd,
        offset = calculateBubblePosition(positionYEmulateButton, positionBubble.height),
        onDismissRequest = {}
    ) {
        LaunchedEffect(Unit) { visibleState = true }
        AnimatedVisibility(
            visible = visibleState,
            enter = fadeIn(animationSpec = tween(DELAY_ANIMATION)),
            exit = fadeOut(animationSpec = tween(DELAY_ANIMATION))
        ) {
            positionBubble = ComposableHoldToSendInternal()
        }
    }
}

@Composable
private fun calculateBubblePosition(
    positionYEmulateButton: Int,
    positionXBubble: Int
): IntOffset {
    return IntOffset(
        x = LocalDensity.current.run { PADDING_END_BUBBLE_FROM_DEVICE.dp.toPx() }.toInt() * -1,
        y = positionYEmulateButton - positionXBubble + DISTANCE_FROM_BUBBLE_BUTTON
    )
}

@Composable
fun ComposableHoldToSendInternal(): IntSize {
    var bubbleSize by remember { mutableStateOf(IntSize.Zero) }
    val bubbleColor = LocalPallet.current.bubbleEmulateBackground.copy(alpha = 0.8f)
    Column(
        modifier = Modifier.onGloballyPositioned { bubbleSize = it.size },
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(bubbleColor)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                text = stringResource(id = R.string.keyscreen_bubble_hold),
                style = LocalTypography.current.titleSB18.copy(fontSize = 12.sp),
                color = LocalPallet.current.bubbleEmulate
            )
        }
        Canvas(
            modifier = Modifier
                .padding(end = PADDING_END_SLICE_FROM_BUBBLE.dp)
                .size(SIZE_BUBBLE.dp)
        ) {
            val trianglePath = Path().apply {
                moveTo(x = 0f, y = 0f)
                lineTo(x = size.width / 2, y = size.height)
                lineTo(x = size.width, y = 0f)
            }

            drawIntoCanvas { canvas ->
                canvas.drawOutline(
                    outline = Outline.Generic(trianglePath),
                    paint = Paint().apply {
                        color = bubbleColor
                        pathEffect = PathEffect.cornerPathEffect(RADIUS_SLICE_UNDER_BUBBLE)
                    }
                )
            }
        }
    }
    return bubbleSize
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableHoldToSendPreview() {
    FlipperThemeInternal {
        ComposableHoldToSendInternal()
    }
}
