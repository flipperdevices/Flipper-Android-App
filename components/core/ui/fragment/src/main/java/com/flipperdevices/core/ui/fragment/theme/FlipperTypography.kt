package com.flipperdevices.core.ui.fragment.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.flipperdevices.core.ui.fragment.R

data class FlipperTypography(
    val titleB20: TextStyle,
    val titleB18: TextStyle,

    val subtitleM12: TextStyle,
    val subtitleB10: TextStyle,
    val subtitleM10: TextStyle,

    val bodyR14: TextStyle,
    val bodyM14: TextStyle,
    val bodyR16: TextStyle,

    val buttonB16: TextStyle,
    val buttonM16: TextStyle,
    val buttonB14: TextStyle
)

internal val fontFamily = FontFamily(
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
