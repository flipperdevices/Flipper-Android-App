package com.flipperdevices.core.ui.res

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val lightPallet = Pallet(
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
val darkPallet = Pallet(
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

val typography = Typography(
    titleB20 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.W700
    ),
    titleB18 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.W700
    ),
    subtitleM12 = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.W500
    ),
    subtitleB10 = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.W700
    ),
    subtitleM10 = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.W500
    ),
    bodyR14 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400
    ),
    bodyM14 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W500
    ),
    bodyR16 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W400
    ),
    buttonB16 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W700
    ),
    buttonM16 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W500
    ),
    buttonB14 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W700
    ),
)
