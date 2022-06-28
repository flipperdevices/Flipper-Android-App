package com.flipperdevices.core.ui.fragment.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.flipperdevices.core.navigation.requireRouter
import com.flipperdevices.core.ui.ktx.LocalRouter

val LocalPallet = compositionLocalOf<FlipperPallet> { error("No local pallet") }
val LocalTypography = compositionLocalOf<FlipperTypography> { error("No local typography") }

@Composable
fun Fragment.FlipperTheme(
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
            LocalRouter provides requireRouter(),
            content = content
        )
    }
}

@Suppress("MagicNumber")
private val lightPallet = FlipperPallet(
    background = Color(0xFFFBFBFB),
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
    surface = Color(0xFFFFFFFF)
)

@Suppress("MagicNumber")
private val darkPallet = FlipperPallet(
    background = Color(0xFF000000),
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
    surface = Color(0xFFFFFFFF)
)

private val typography = FlipperTypography(
    titleB20 = TextStyle(
        fontSize = 20.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700
    ),
    titleB18 = TextStyle(
        fontSize = 18.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700
    ),
    subtitleM12 = TextStyle(
        fontSize = 12.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500
    ),
    subtitleB10 = TextStyle(
        fontSize = 10.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700
    ),
    subtitleM10 = TextStyle(
        fontSize = 10.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500
    ),
    bodyR14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400
    ),
    bodyM14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500
    ),
    bodyR16 = TextStyle(
        fontSize = 16.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W400
    ),
    buttonB16 = TextStyle(
        fontSize = 16.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700
    ),
    buttonM16 = TextStyle(
        fontSize = 16.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W500
    ),
    buttonB14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.W700
    )
)
