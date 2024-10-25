package com.flipperdevices.core.ui.ktx.image

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.ui.ktx.elements.FlipperProgressIndicator

@Composable
fun AndroidFlipperProgressIndicator(
    accentColor: Color,
    secondColor: Color,
    @DrawableRes iconId: Int?,
    percent: Float?,
    modifier: Modifier = Modifier
) {
    FlipperProgressIndicator(
        accentColor = accentColor,
        secondColor = secondColor,
        painter = iconId?.let { painterResourceByKey(iconId) },
        percent = percent,
        modifier = modifier
    )
}
