package com.flipperdevices.connection.impl.composable.status

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.connection.impl.R
import com.flipperdevices.connection.impl.composable.status.helper.rotatableSweepGradient
import com.flipperdevices.connection.impl.model.ConnectionStatusState

private const val PROGRESS_BAR_END_PERCENT = 0.2f
private const val PROGRESS_BAR_END_DURATION_MS = 1000

@Composable
fun ComposableConnectionBackground(
    modifier: Modifier = Modifier,
    statusState: ConnectionStatusState,
    content: @Composable () -> Unit
) {
    val brush = getBrush(statusState)

    Box(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .clip(RoundedCornerShape(CornerSize(percent = 100)))
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(all = 3.dp)
                .clip(RoundedCornerShape(CornerSize(percent = 100)))
                .background(Color.White)
        ) {
            Box(modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun getBrush(statusState: ConnectionStatusState): Brush {
    return when (statusState) {
        ConnectionStatusState.Disconnected ->
            SolidColor(colorResource(R.color.state_border_not_connected_color))
        ConnectionStatusState.Connecting ->
            getAnimatedBrush(
                firstColor = colorResource(R.color.state_border_connecting_first_color),
                secondColor = colorResource(R.color.state_border_connecting_second_color)
            )
        ConnectionStatusState.Synchronization ->
            getAnimatedBrush(
                firstColor = colorResource(R.color.state_border_synchronization_first_color),
                secondColor = colorResource(R.color.state_border_synchronization_second_color)
            )
        is ConnectionStatusState.Completed ->
            SolidColor(colorResource(R.color.state_border_connected_color))
    }
}

@Composable
private fun getAnimatedBrush(
    firstColor: Color,
    secondColor: Color
): Brush {
    val rotationTransition = rememberInfiniteTransition()
    val angelAnimated by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = PROGRESS_BAR_END_DURATION_MS,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    val colorStops = listOf(
        0f to secondColor,
        PROGRESS_BAR_END_PERCENT to firstColor
    ).toTypedArray()

    return rotatableSweepGradient(
        colorStops = colorStops,
        angel = angelAnimated
    )
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun PreviewComposableConnectionBackground() {
    ComposableConnectionBackground(
        statusState = ConnectionStatusState.Connecting
    ) {
        Text(text = "Sample")
    }
}
