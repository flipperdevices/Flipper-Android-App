package com.flipperdevices.wearable.emulate.impl.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyscreen.api.EmulateProgress
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import com.flipperdevices.keyscreen.api.Picture
import com.flipperdevices.wearable.emulate.impl.R

@Composable
fun ComposableWearEmulate(keyEmulateUiApi: KeyEmulateUiApi) {
    keyEmulateUiApi.ComposableEmulateButtonRaw(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        buttonContentModifier = Modifier,
        emulateProgress = EmulateProgress.Infinite,
        picture = Picture.LottieRes(
            picResId = DesignSystem.raw.ic_emulating,
            fallBackPicResId = DesignSystem.drawable.ic_emulate
        ),
        textId = R.string.keyscreen_emulating,
        color = LocalPallet.current.actionOnFlipperProgress,
        progressColor = LocalPallet.current.actionOnFlipperEnable
    )
}
