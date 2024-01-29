package com.flipperdevices.wearable.core.ui.ktx

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.MaterialTheme.colors
import androidx.wear.compose.material.Shapes
import androidx.wear.compose.material.contentColorFor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.core.ui.theme.composable.FlipperPallet
import com.flipperdevices.core.ui.theme.composable.getThemedFlipperPallet
import com.flipperdevices.core.ui.theme.composable.getTypography

@Composable
fun WearFlipperTheme(
    content: @Composable () -> Unit
) {
    val pallet = getThemedFlipperPallet(isLight = false)

    val shapes = Shapes(medium = RoundedCornerShape(size = 10.dp))

    MaterialTheme(
        colors = pallet.toWearMaterialColors(),
        shapes = shapes
    ) {
        CompositionLocalProvider(
            LocalPallet provides pallet,
            LocalTypography provides getTypography(),
            LocalContentColor provides colors.contentColorFor(backgroundColor = pallet.background),
            content = content
        )
    }
}

private fun FlipperPallet.toWearMaterialColors() = Colors(
    primary = content,
    primaryVariant = accentSecond,
    secondary = content,
    secondaryVariant = accent,
    background = background,
    surface = content,
    error = error,
    onPrimary = onContent,
    onSecondary = onContent,
    onBackground = onContent,
    onSurface = onContent,
    onError = onError
)
