package com.flipperdevices.keyscreen.emulate.composable.common.button

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.api.EmulateProgress
import com.flipperdevices.keyscreen.api.Picture
import com.flipperdevices.keyscreen.emulate.R
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val TOTAL_PROGRESS_PREVIEW = 1000L

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
@Suppress("LongMethod")
private fun ComposableEmulateButtonPreview() {
    FlipperThemeInternal {
        Column {
            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                emulateProgress = EmulateProgress.Infinite,
                picture = Picture.LottieRes(
                    picResId = DesignSystem.raw.ic_emulating,
                    fallBackPicResId = DesignSystem.drawable.ic_emulate
                ),
                textId = R.string.keyscreen_emulating,
                color = LocalPallet.current.actionOnFlipperProgress,
                progressColor = LocalPallet.current.actionOnFlipperEnable
            )

            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                picture = Picture.StaticRes(DesignSystem.drawable.ic_emulate),
                textId = R.string.keyscreen_emulate,
                color = LocalPallet.current.actionOnFlipperEnable
            )

            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                emulateProgress = EmulateProgress.Infinite,
                picture = Picture.LottieRes(
                    picResId = DesignSystem.raw.ic_sending,
                    fallBackPicResId = DesignSystem.drawable.ic_send
                ),
                textId = R.string.keyscreen_sending,
                color = LocalPallet.current.actionOnFlipperSubGhzProgress,
                progressColor = LocalPallet.current.actionOnFlipperSubGhzEnable
            )

            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                picture = Picture.StaticRes(DesignSystem.drawable.ic_send),
                textId = R.string.keyscreen_send,
                color = LocalPallet.current.actionOnFlipperSubGhzEnable
            )

            ComposableEmulateButton(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                emulateProgress = EmulateProgress.Growing(TOTAL_PROGRESS_PREVIEW),
                picture = Picture.LottieRes(
                    picResId = DesignSystem.raw.ic_sending,
                    fallBackPicResId = DesignSystem.drawable.ic_send
                ),
                textId = R.string.keyscreen_sending,
                color = LocalPallet.current.actionOnFlipperSubGhzProgress,
                progressColor = LocalPallet.current.actionOnFlipperSubGhzEnable
            )
        }
    }
}
