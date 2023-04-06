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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenSnapshot
import com.flipperdevices.screenstreaming.impl.model.ScreenOrientationEnum

private const val VERTICAL_ORIENTATION_ANGEL = 90f

@Composable
fun ComposableFlipperScreenWithOptions(
    flipperScreen: FlipperScreenSnapshot,
    onTakeScreenshot: () -> Unit,
    modifier: Modifier = Modifier
) = BoxWithConstraints(
    modifier = modifier.padding(top = 14.dp, bottom = 24.dp),
    contentAlignment = Alignment.Center
) {
    val angel by animateFloatAsState(
        targetValue = when (flipperScreen.orientation) {
            ScreenOrientationEnum.HORIZONTAL,
            ScreenOrientationEnum.HORIZONTAL_FLIP -> 0f
            ScreenOrientationEnum.VERTICAL -> VERTICAL_ORIENTATION_ANGEL
        }
    )
    val height by animateDpAsState(
        targetValue = when (flipperScreen.orientation) {
            ScreenOrientationEnum.HORIZONTAL,
            ScreenOrientationEnum.HORIZONTAL_FLIP -> maxHeight
            ScreenOrientationEnum.VERTICAL -> maxWidth
        }
    )

    val width by animateDpAsState(
        targetValue = when (flipperScreen.orientation) {
            ScreenOrientationEnum.HORIZONTAL,
            ScreenOrientationEnum.HORIZONTAL_FLIP -> maxWidth
            ScreenOrientationEnum.VERTICAL -> maxHeight
        }
    )

    Column(
        modifier = Modifier
            .size(height = height, width = width)
            .rotate(angel),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ComposableFlipperScreenOptions(angel, onTakeScreenshot)

        ComposableFlipperScreen(
            bitmap = flipperScreen.bitmap,
            showLogo = flipperScreen.orientation != ScreenOrientationEnum.VERTICAL
        )
    }
}

@Composable
private fun ColumnScope.ComposableFlipperScreenOptions(
    angel: Float,
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
            var isLock by remember { mutableStateOf(false) }
            ComposableFlipperScreenLock(
                modifier = optionsModifier,
                isLock = isLock,
                onChangeState = { isLock = it }
            )
            ComposableFlipperScreenScreenshot(
                modifier = optionsModifier,
                onClick = onTakeScreenshot
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableFlipperScreenWithOptionsHorizontalPreview() {
    FlipperThemeInternal {
        ComposableFlipperScreenWithOptions(
            flipperScreen = FlipperScreenSnapshot(
                orientation = ScreenOrientationEnum.HORIZONTAL
            ),
            onTakeScreenshot = {}
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableFlipperScreenWithOptionsVerticalPreview() {
    FlipperThemeInternal {
        ComposableFlipperScreenWithOptions(
            flipperScreen = FlipperScreenSnapshot(
                orientation = ScreenOrientationEnum.VERTICAL
            ),
            onTakeScreenshot = {}
        )
    }
}