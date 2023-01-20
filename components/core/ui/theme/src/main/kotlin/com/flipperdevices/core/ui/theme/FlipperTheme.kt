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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.ui.theme.composable.getThemedFlipperPallet
import com.flipperdevices.core.ui.theme.composable.getTypography
import com.flipperdevices.core.ui.theme.composable.isLight
import com.flipperdevices.core.ui.theme.composable.setAppCompatDelegateTheme
import com.flipperdevices.core.ui.theme.models.FlipperPallet
import com.flipperdevices.core.ui.theme.models.FlipperTypography
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel

val LocalPallet = compositionLocalOf<FlipperPallet> { error("No local pallet") }
val LocalTypography = compositionLocalOf<FlipperTypography> { error("No local typography") }

@Composable
fun FlipperTheme(
    content: @Composable () -> Unit,
    themeViewModel: ThemeViewModel = viewModel()
) {
    val theme by themeViewModel.getAppTheme().collectAsState()
    val isLight = isLight(systemIsDark = isSystemInDarkTheme())
    FlipperThemeInternal(
        content = content,
        theme = theme,
        isLight = isLight
    )
}

// For preview composable function, because we don`t provide LocalPallet/Typography/MaterialDesign
@Composable
fun FlipperThemeInternal(
    theme: SelectedTheme = SelectedTheme.SYSTEM,
    isLight: Boolean = !isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val pallet = getThemedFlipperPallet(theme, isLight)
    val colors = pallet.toMaterialColors(isLight)
    val shapes = Shapes(medium = RoundedCornerShape(size = 10.dp))

    LaunchedEffect(key1 = theme) { setAppCompatDelegateTheme(theme) }

    MaterialTheme(
        shapes = shapes,
        colors = colors
    ) {
        CompositionLocalProvider(
            LocalPallet provides pallet,
            LocalTypography provides getTypography(),
            LocalContentColor provides colors.contentColorFor(backgroundColor = pallet.background),
            LocalTextSelectionColors provides pallet.toTextSelectionColors(),
            LocalIndication provides NoIndication,
            content = content
        )
    }
}

/*
    Standardization of the indication for all clickable modifiers
 */
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
