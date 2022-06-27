package com.flipperdevices.core.ui.res

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val lightPallet = Pallet(
    accent = Accent,
    accentSecondary = AccentSecond,
    background = WhiteBackground,
    text_100 = Black100,
    text_88 = Black88,
    text_80 = Black80,
    text_60 = Black60,
    text_40 = Black40,
    text_30 = Black30,
    text_20 = Black20,
    text_16 = Black16,
    text_12 = Black12,
    text_8 = Black8,
    text_4 = Black4,
    surface = White100
)
val darkPallet = Pallet(
    accent = Accent,
    accentSecondary = AccentSecond,
    background = BlackBackground,
    text_100 = Black100,
    text_88 = Black88,
    text_80 = Black80,
    text_60 = Black60,
    text_40 = Black40,
    text_30 = Black30,
    text_20 = Black20,
    text_16 = Black16,
    text_12 = Black12,
    text_8 = Black8,
    text_4 = Black4,
    surface = White100
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
