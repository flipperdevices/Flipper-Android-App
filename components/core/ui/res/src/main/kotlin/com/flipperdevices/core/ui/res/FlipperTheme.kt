package com.flipperdevices.core.ui.res

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

val LocalPallet = compositionLocalOf<Pallet> { error("No local pallet") }
val LocalTypography = compositionLocalOf<Typography> { error("No local typography") }

@Composable
fun FlipperTheme(
    content: @Composable () -> Unit
) {
    val pallet = if (isSystemInDarkTheme()) darkPallet else lightPallet

    CompositionLocalProvider(
        LocalPallet provides pallet,
        LocalTypography provides typography
    ) {
        MaterialTheme(
            content = content,
            shapes = Shapes(
                medium = RoundedCornerShape(size = 10.dp),
            ),
            colors = pallet.toMaterialColors(),
        )
    }
}
