package com.flipperdevices.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.res.R

val LocalPallet = compositionLocalOf<FlipperPallet> { error("No local pallet") }
val LocalTypography = compositionLocalOf<FlipperTypography> { error("No local typography") }

@Composable
fun FlipperTheme(
    isLight: Boolean,
    content: @Composable () -> Unit
) {
    val pallet = if (isLight) darkPallet else lightPallet

    MaterialTheme(
        shapes = Shapes(
            medium = RoundedCornerShape(size = 10.dp)
        ),
        colors = pallet.toMaterialColors(isLight)
    ) {
        CompositionLocalProvider(
            LocalPallet provides pallet,
            LocalTypography provides typography,
            content = content
        )
    }
}

@Suppress("MagicNumber")
private val lightPallet = FlipperPallet(
    background = Color(0xFFFBFBFB),
    text100 = Color(0xFF000000),
    onButton100 = Color(0xFFFFFFFF),
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

    deletedCategoryType = Color(0xFFE9E9E9),
    keyDeleted = Color(0xFFE9E9E9),
    progressBar = Color(0xFFAAAAAA),

    selectedBottomBar = Color(0xFF303030),
    unselectedBottomBar40 = Color(0xFF919191),
    unselectedBottomBar30 = Color(0xFFAAAAAA),
    backgroundBottomBar = Color(0xFFE9E9E9),

    unsynchronized = Color(0xFFAAAAAA),
    divider12 = Color(0xFFDFDFDF),
    notificationCard = Color(0xFFE9E9E9),

    batteryBackground = Color(0xFF8D8E92),
    placeholder = Color(0xFFDFDFDF),
    switchUncheckedTrack = Color(0xFF919191),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000)
)

@Suppress("MagicNumber")
private val darkPallet = FlipperPallet(
    background = Color(0xFFFBFBFB),
    onButton100 = Color(0xFFFFFFFF),
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

    deletedCategoryType = Color(0xFFE9E9E9),
    keyDeleted = Color(0xFFE9E9E9),
    progressBar = Color(0xFFAAAAAA),

    selectedBottomBar = Color(0xFF303030),
    unselectedBottomBar40 = Color(0xFF919191),
    unselectedBottomBar30 = Color(0xFFAAAAAA),
    backgroundBottomBar = Color(0xFFE9E9E9),

    unsynchronized = Color(0xFFAAAAAA),
    divider12 = Color(0xFFDFDFDF),
    notificationCard = Color(0xFFE9E9E9),

    batteryBackground = Color(0xFF8D8E92),
    placeholder = Color(0xFFDFDFDF),
    switchUncheckedTrack = Color(0xFF919191),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000)
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
