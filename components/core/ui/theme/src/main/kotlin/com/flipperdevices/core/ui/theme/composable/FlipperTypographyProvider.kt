package com.flipperdevices.core.ui.theme.composable

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flipperdevices.core.ui.res.R
import com.flipperdevices.core.ui.theme.models.FlipperTypography
import com.flipperdevices.core.ui.theme.models.robotoFamily

fun getTypography(): FlipperTypography {
    return typography
}

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
    titleSB18 = TextStyle(
        fontSize = 18.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W600
    ),
    titleSB16 = TextStyle(
        fontSize = 18.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W600
    ),
    titleEB20 = TextStyle(
        fontSize = 20.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W800
    ),
    titleEB18 = TextStyle(
        fontSize = 18.sp,
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
    subtitleEB12 = TextStyle(
        fontSize = 12.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W800
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
    subtitleR10 = TextStyle(
        fontSize = 10.sp,
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
    bodySB14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W600
    ),
    bodySSB14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = robotoFamily,
        fontWeight = FontWeight.W700
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
    ),
    flipperAction = TextStyle(
        fontSize = 24.sp,
        fontFamily = FontFamily(Font(R.font.flipper_action)),
        fontWeight = FontWeight.W500
    ),
    monoSpaceM14 = TextStyle(
        fontSize = 14.sp,
        fontFamily = FontFamily(Font(R.font.roboto_mono_medium)),
        fontWeight = FontWeight.W500
    ),
    monoSpaceM10 = TextStyle(
        fontSize = 10.sp,
        fontFamily = FontFamily(Font(R.font.roboto_mono_medium)),
        fontWeight = FontWeight.W500
    ),
    monoSpaceM12 = TextStyle(
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(R.font.roboto_mono_medium)),
        fontWeight = FontWeight.W500
    ),
    monoSpaceM16 = TextStyle(
        fontSize = 16.sp,
        fontFamily = FontFamily(Font(R.font.roboto_mono_medium)),
        fontWeight = FontWeight.W500
    ),
    monoSpaceR12 = TextStyle(
        fontSize = 12.sp,
        fontFamily = FontFamily(Font(R.font.roboto_mono_medium)),
        fontWeight = FontWeight.W400
    )
)
