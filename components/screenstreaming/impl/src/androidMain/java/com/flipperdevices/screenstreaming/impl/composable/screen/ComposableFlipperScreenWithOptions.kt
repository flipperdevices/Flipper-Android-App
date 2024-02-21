package com.flipperdevices.screenstreaming.impl.composable.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.screenstreaming.impl.model.FlipperButtonStackElement
import com.flipperdevices.screenstreaming.impl.model.FlipperLockState
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenState
import com.flipperdevices.screenstreaming.impl.model.ScreenOrientationEnum
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val VERTICAL_ORIENTATION_ANGEL = 90f

/**
 * Now this animation is made difficult.
 * In the future it can be simplified if Google adds "Animation: Shared element transitions".
 * This is already in RoadMap
 */
@Composable
fun ComposableFlipperScreenWithOptions(
    flipperScreen: FlipperScreenState,
    buttons: ImmutableList<FlipperButtonStackElement>,
    onTakeScreenshot: () -> Unit,
    lockState: FlipperLockState,
    onClickLockButton: () -> Unit,
    modifier: Modifier = Modifier
) = BoxWithConstraints(
    modifier = modifier.padding(top = 14.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
    contentAlignment = Alignment.Center
) {
    val isHorizontal = remember(flipperScreen) {
        when (flipperScreen) {
            FlipperScreenState.InProgress,
            FlipperScreenState.NotConnected -> true
            is FlipperScreenState.Ready -> when (flipperScreen.orientation) {
                ScreenOrientationEnum.HORIZONTAL,
                ScreenOrientationEnum.HORIZONTAL_FLIP -> true
                ScreenOrientationEnum.VERTICAL,
                ScreenOrientationEnum.VERTICAL_FLIP -> false
            }
        }
    }
    val angel by animateFloatAsState(
        targetValue = if (isHorizontal) 0f else VERTICAL_ORIENTATION_ANGEL
    )
    val height by animateDpAsState(
        targetValue = if (isHorizontal) maxHeight else maxWidth
    )

    val width by animateDpAsState(
        targetValue = if (isHorizontal) maxWidth else maxHeight
    )

    Column(
        modifier = Modifier
            .size(height = height, width = width)
            .rotate(angel),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ComposableFlipperScreenOptions(
            angel = angel,
            onTakeScreenshot = onTakeScreenshot,
            lockState = lockState,
            onClickLockButton = onClickLockButton
        )

        ComposableFlipperScreen(
            buttons = buttons,
            flipperScreen = flipperScreen,
            isHorizontal = isHorizontal
        )
    }
}

@Composable
private fun ColumnScope.ComposableFlipperScreenOptions(
    angel: Float,
    lockState: FlipperLockState,
    onClickLockButton: () -> Unit,
    onTakeScreenshot: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
    ) {
        val rotatePercent = angel / VERTICAL_ORIENTATION_ANGEL
        val topPadding = (maxHeight / 2) * rotatePercent
        val optionsModifier = Modifier
            .rotate(-angel)
            .padding(
                top = topPadding
            )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 36.dp, end = 36.dp)
                .offset(x = -(topPadding / 2)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            ComposableFlipperScreenScreenshot(
                modifier = optionsModifier,
                onClick = onTakeScreenshot
            )
            ComposableFlipperScreenLock(
                modifier = optionsModifier,
                lockState = lockState,
                onChangeState = onClickLockButton
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFlipperScreenWithOptionsHorizontalPreview() {
    FlipperThemeInternal {
        ComposableFlipperScreenWithOptions(
            flipperScreen = FlipperScreenState.NotConnected,
            buttons = persistentListOf(),
            onTakeScreenshot = {},
            lockState = FlipperLockState.NotInitialized,
            onClickLockButton = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFlipperScreenWithOptionsVerticalPreview() {
    FlipperThemeInternal {
        ComposableFlipperScreenWithOptions(
            flipperScreen = FlipperScreenState.NotConnected,
            buttons = persistentListOf(),
            onTakeScreenshot = {},
            lockState = FlipperLockState.NotInitialized,
            onClickLockButton = {}
        )
    }
}
