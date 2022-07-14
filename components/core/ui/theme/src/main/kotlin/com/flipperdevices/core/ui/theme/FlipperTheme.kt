package com.flipperdevices.core.ui.theme

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.models.FlipperPallet
import com.flipperdevices.core.ui.theme.models.FlipperTypography
import com.flipperdevices.core.ui.theme.models.robotoFamily
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel

val LocalPallet = compositionLocalOf<FlipperPallet> { error("No local pallet") }
val LocalTypography = compositionLocalOf<FlipperTypography> { error("No local typography") }

@Composable
fun FlipperTheme(
    themeViewModel: ThemeViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val theme by themeViewModel.getAppTheme().collectAsState()
    val isLight = isLight(theme)

    val pallet = if (isLight) lightPallet else darkPallet
    val colors = pallet.toMaterialColors(isLight)
    val shapes = Shapes(medium = RoundedCornerShape(size = 10.dp))

    DisposableEffect(key1 = theme) {
        val systemThemeId = when (theme) {
            SelectedTheme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            SelectedTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(systemThemeId)
        onDispose { }
    }

    MaterialTheme(
        shapes = shapes,
        colors = colors
    ) {
        CompositionLocalProvider(
            LocalPallet provides pallet,
            LocalTypography provides typography,
            LocalContentColor provides colors.contentColorFor(backgroundColor = pallet.background),
            content = content
        )
    }
}

@Composable
private fun isLight(theme: SelectedTheme): Boolean {
    return when (theme) {
        SelectedTheme.LIGHT -> true
        SelectedTheme.DARK -> false
        else -> !isSystemInDarkTheme()
    }
}

@Suppress("MagicNumber")
private val lightPallet = FlipperPallet(
    background = Color(0xFFFBFBFB),
    error = Color(0xFFFFFFFF),
    content = Color(0xFFFFFFFF),
    onContent = Color(0xFF000000),

    text100 = Color(0xFF000000),
    text88 = Color(0xFF1C1C1C),
    text80 = Color(0xFF303030),
    text60 = Color(0xFF616161),
    text40 = Color(0xFF919191),
    text30 = Color(0xFFAAAAAA),
    text20 = Color(0xFFC1C1C1),
    text16 = Color(0xFFCCCCCC),
    text12 = Color(0xFFD6D6D6),
    text8 = Color(0xFFDFDFDF),
    text4 = Color(0xFFE9E9E9),

    iconTint100 = Color(0xFF000000),
    iconTint80 = Color(0xFF303030),
    iconTint30 = Color(0xFFAAAAAA),
    iconTint16 = Color(0xFFCCCCCC),

    divider12 = Color(0xFFDFDFDF),
    channelFirmwareReleaseCandidate = Color(0xFF8A2BE2),
    bottomBarBackground = Color(0xFFFFFFFF),
    disableSwitch = Color(0xFFDFDFDF),
    backgroundDialog = Color(0xFFFFFFFF)
)

@Suppress("MagicNumber")
private val darkPallet = FlipperPallet(
    background = Color(0xFF000000),
    error = Color(0xFF000000),
    content = Color(0xFF1C1C1C),
    onContent = Color(0xFFFFFFFF),

    text100 = Color(0xFFFFFFFF),
    text88 = Color(0XFFE9E9E9),
    text80 = Color(0XFFDFDFDF),
    text60 = Color(0xFFD6D6D6),
    text40 = Color(0xFFCCCCCC),
    text30 = Color(0xFFC1C1C1),
    text20 = Color(0xFFAAAAAA),
    text16 = Color(0xFF919191),
    text12 = Color(0xFF616161),
    text8 = Color(0xFF616161),
    text4 = Color(0xFF1C1C1C),

    iconTint100 = Color(0xFFFFFFFF),
    iconTint80 = Color(0XFFDFDFDF),
    iconTint30 = Color(0xFFC1C1C1),
    iconTint16 = Color(0xFF919191),

    divider12 = Color(0xFF616161),
    channelFirmwareReleaseCandidate = Color(0xFFAA69FA),
    bottomBarBackground = Color(0xFF1C1C1C),
    disableSwitch = Color(0xFF616161),
    backgroundDialog = Color(0xFF303030)
)

private val typography = FlipperTypography(
    titleB24 = TextStyle(
        fontSize = 24.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
    ),
    titleB22 = TextStyle(
        fontSize = 22.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
    ),
    titleB20 = TextStyle(
        fontSize = 20.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
    ),
    titleEB20 = TextStyle(
        fontSize = 20.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W800
    ),
    titleB18 = TextStyle(
        fontSize = 18.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
    ),
    titleR18 = TextStyle(
        fontSize = 18.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W400
    ),
    titleM18 = TextStyle(
        fontSize = 18.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W500
    ),
    subtitleB12 = TextStyle(
        fontSize = 12.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
    ),
    subtitleM12 = TextStyle(
        fontSize = 12.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W500
    ),
    subtitleR12 = TextStyle(
        fontSize = 12.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W400
    ),
    subtitleB10 = TextStyle(
        fontSize = 10.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
    ),
    subtitleM10 = TextStyle(
        fontSize = 10.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W500
    ),
    bodyR14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W400
    ),
    bodyM14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W500
    ),
    bodyR16 = TextStyle(
        fontSize = 16.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W400
    ),
    buttonB16 = TextStyle(
        fontSize = 16.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
    ),
    buttonM16 = TextStyle(
        fontSize = 16.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W500
    ),
    buttonB14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
    ),
    updateButton40 = TextStyle(
        fontSize = 40.sp,
        fontFamily = FontFamily(Font(R.font.flipper_bold)),
        fontWeight = FontWeight.W400
    ),
    updateText40 = TextStyle(
        fontSize = 40.sp,
        fontFamily = FontFamily(Font(R.font.flipper)),
        fontWeight = FontWeight.W400
    )
)
