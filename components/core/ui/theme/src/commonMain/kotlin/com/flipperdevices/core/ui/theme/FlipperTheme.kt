package com.flipperdevices.core.ui.theme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.composable.FlipperPallet
import com.flipperdevices.core.ui.theme.composable.FlipperTypography
import com.flipperdevices.core.ui.theme.composable.getThemedFlipperPallet
import com.flipperdevices.core.ui.theme.composable.getTypography
import com.flipperdevices.core.ui.theme.composable.isLight
import com.flipperdevices.core.ui.theme.composable.pallet.generated.FlipperPalletV2
import com.flipperdevices.core.ui.theme.composable.pallet.getThemedFlipperPalletV2
import com.flipperdevices.core.ui.theme.composable.toMaterialColors
import com.flipperdevices.core.ui.theme.composable.toTextSelectionColors
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel

val LocalPallet = compositionLocalOf<FlipperPallet> { error("No local pallet") }
val LocalPalletV2 = compositionLocalOf<FlipperPalletV2> { error("No local pallet") }
val LocalTypography = compositionLocalOf<FlipperTypography> { error("No local typography") }

@Composable
fun FlipperTheme(
    themeViewModel: ThemeViewModel,
    content: @Composable () -> Unit,
) {
    val theme by themeViewModel.getAppTheme().collectAsState()
    val isLight = isLight(
        systemIsDark = isSystemInDarkTheme(),
        theme = theme
    )
    val pallet = getThemedFlipperPallet(isLight)
    FlipperTheme(
        content = content,
        pallet = pallet,
        palletV2 = getThemedFlipperPalletV2(isLight),
        isLight = isLight
    )
}

@Composable
private fun FlipperTheme(
    pallet: FlipperPallet,
    palletV2: FlipperPalletV2,
    isLight: Boolean,
    content: @Composable () -> Unit
) {
    val colors = pallet.toMaterialColors(isLight)
    val shapes = Shapes(medium = RoundedCornerShape(size = 10.dp))

    MaterialTheme(
        shapes = shapes,
        colors = colors
    ) {
        CompositionLocalProvider(
            LocalPallet provides pallet,
            LocalPalletV2 provides palletV2,
            LocalTypography provides getTypography(),
            LocalContentColor provides colors.contentColorFor(backgroundColor = pallet.background),
            LocalTextSelectionColors provides pallet.toTextSelectionColors(),
            LocalIndication provides NoIndication,
            content = content
        )
    }
}

// For preview composable function, because we don`t provide LocalPallet/Typography/MaterialDesign
@Composable
fun FlipperThemeInternal(
    content: @Composable () -> Unit
) {
    FlipperTheme(
        pallet = getThemedFlipperPallet(!isSystemInDarkTheme()),
        palletV2 = getThemedFlipperPalletV2(!isSystemInDarkTheme()),
        content = content,
        isLight = !isSystemInDarkTheme()
    )
}

/**
 * Standardization of the indication for all clickable modifiers
 **/
private object NoIndication : Indication {
    private object NoIndicationInstance : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        return NoIndicationInstance
    }
}
