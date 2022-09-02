package com.flipperdevices.keyscreen.emulate.composable.common.button

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.emulate.model.EmulateProgress
import com.flipperdevices.keyscreen.emulate.model.Picture

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableEmulateButtonPreview() {
    FlipperThemeInternal() {
        Column() {
            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                emulateProgress = EmulateProgress.Infinite,
                picture = Picture.LottieRes(
                    picResId = R.raw.ic_emulating,
                    fallBackPicResId = R.drawable.ic_emulate
                ),
                textId = com.flipperdevices.keyscreen.emulate.R.string.keyscreen_emulating,
                color = LocalPallet.current.actionOnFlipperProgress,
                progressColor = LocalPallet.current.actionOnFlipperEnable
            )

            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                picture = Picture.StaticRes(R.drawable.ic_emulate),
                textId = com.flipperdevices.keyscreen.emulate.R.string.keyscreen_emulate,
                color = LocalPallet.current.actionOnFlipperEnable
            )

            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                emulateProgress = EmulateProgress.Infinite,
                picture = Picture.LottieRes(
                    picResId = R.raw.ic_sending,
                    fallBackPicResId = R.drawable.ic_send
                ),
                textId = com.flipperdevices.keyscreen.emulate.R.string.keyscreen_sending,
                color = LocalPallet.current.actionOnFlipperSubGhzProgress,
                progressColor = LocalPallet.current.actionOnFlipperSubGhzEnable
            )

            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                picture = Picture.StaticRes(R.drawable.ic_send),
                textId = com.flipperdevices.keyscreen.emulate.R.string.keyscreen_send,
                color = LocalPallet.current.actionOnFlipperSubGhzEnable
            )

            val fixedProgress by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 5000,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                emulateProgress = EmulateProgress.Fixed((fixedProgress * 1000).toLong(), 1000),
                picture = Picture.StaticRes(R.drawable.ic_emulate),
                textId = com.flipperdevices.keyscreen.emulate.R.string.keyscreen_emulating,
                color = LocalPallet.current.actionOnFlipperProgress,
                progressColor = LocalPallet.current.actionOnFlipperEnable
            )
        }
    }
}
