package com.flipperdevices.core.ui.fragment

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

object FontFamilyFactory {
    val Roboto = FontFamily(
        fonts = listOf(
            Font(R.font.roboto_regular, FontWeight.Normal),
            Font(R.font.roboto_italic, FontWeight.Normal, FontStyle.Italic),
            Font(R.font.roboto_black, FontWeight.Black),
            Font(R.font.roboto_black_italic, FontWeight.Black, FontStyle.Italic),
            Font(R.font.roboto_light, FontWeight.Light),
            Font(R.font.roboto_light_italic, FontWeight.Light, FontStyle.Italic),
            Font(R.font.roboto_medium, FontWeight.Medium),
            Font(R.font.roboto_medium_italic, FontWeight.Medium, FontStyle.Italic),
            Font(R.font.roboto_bold, FontWeight.Bold),
            Font(R.font.roboto_bold_italic, FontWeight.Bold, FontStyle.Italic),
            Font(R.font.roboto_thin, FontWeight.Thin),
            Font(R.font.roboto_thin_italic, FontWeight.Thin, FontStyle.Italic)
        )
    )

    fun getTypographyWithReplacedFontFamily(
        fontFamily: FontFamily,
        typography: Typography
    ): Typography {
        return Typography(
            h1 = typography.h1.copy(fontFamily = fontFamily),
            h2 = typography.h2.copy(fontFamily = fontFamily),
            h3 = typography.h3.copy(fontFamily = fontFamily),
            h4 = typography.h4.copy(fontFamily = fontFamily),
            h5 = typography.h5.copy(fontFamily = fontFamily),
            h6 = typography.h6.copy(fontFamily = fontFamily),
            subtitle1 = typography.subtitle1.copy(fontFamily = fontFamily),
            subtitle2 = typography.subtitle2.copy(fontFamily = fontFamily),
            body1 = typography.body1.copy(fontFamily = fontFamily),
            body2 = typography.body2.copy(fontFamily = fontFamily),
            button = typography.button.copy(fontFamily = fontFamily),
            caption = typography.caption.copy(fontFamily = fontFamily),
            overline = typography.overline.copy(fontFamily = fontFamily)
        )
    }
}
