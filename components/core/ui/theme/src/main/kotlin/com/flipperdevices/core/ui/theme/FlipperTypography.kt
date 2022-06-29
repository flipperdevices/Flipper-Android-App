package com.flipperdevices.core.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.flipperdevices.core.ui.res.R

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
    Font(R.font.roboto_regular, FontWeight.W400),
    Font(R.font.roboto_italic, FontWeight.W400, FontStyle.Italic),
    Font(R.font.roboto_black, FontWeight.W900),
    Font(R.font.roboto_black_italic, FontWeight.W900, FontStyle.Italic),
    Font(R.font.roboto_light, FontWeight.W300),
    Font(R.font.roboto_light_italic, FontWeight.W300, FontStyle.Italic),
    Font(R.font.roboto_medium, FontWeight.W500),
    Font(R.font.roboto_medium_italic, FontWeight.W500, FontStyle.Italic),
    Font(R.font.roboto_bold, FontWeight.W700),
    Font(R.font.roboto_bold_italic, FontWeight.W700, FontStyle.Italic),
    Font(R.font.roboto_thin, FontWeight.W100),
    Font(R.font.roboto_thin_italic, FontWeight.W100, FontStyle.Italic)
)
