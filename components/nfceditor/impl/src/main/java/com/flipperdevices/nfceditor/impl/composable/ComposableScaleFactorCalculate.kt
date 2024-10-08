package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

const val WIDTH_LINE_INDEX_DP = 9

private const val NFC_LINE_BYTE_COUNT = 16

// The accuracy with which we select the scaleFactor
private const val SCALE_FACTOR_MULTIPLIER = 0.98f

/**
 * @param maxIndexCount How many numbers can be in the number of lines (line index)
 */
@Composable
fun BoxWithConstraintsScope.calculateScaleFactor(maxIndexCount: Int): Float {
    var currentScaleFactor = 1.0f

    if (shouldShrink(maxIndexCount, currentScaleFactor)) {
        do {
            currentScaleFactor *= SCALE_FACTOR_MULTIPLIER
        } while (shouldShrink(maxIndexCount, currentScaleFactor))
        return currentScaleFactor
    } else {
        var previousScaleFactor: Float
        do {
            previousScaleFactor = currentScaleFactor
            currentScaleFactor /= SCALE_FACTOR_MULTIPLIER
        } while (!shouldShrink(maxIndexCount, currentScaleFactor))
        return previousScaleFactor
    }
}

@Composable
private fun BoxWithConstraintsScope.shouldShrink(maxIndexCount: Int, scaleFactor: Float): Boolean {
    val textStyle = LocalTextStyle.current
    val textMeasurer = rememberTextMeasurer()

    val otherWidthsDpWithoutScaleFactor = (maxIndexCount * WIDTH_LINE_INDEX_DP) +
        (PADDING_CELL_DP * 2 * NFC_LINE_BYTE_COUNT)

    val otherWidthsPx = with(LocalDensity.current) {
        (otherWidthsDpWithoutScaleFactor * scaleFactor).dp.toPx()
    }

    val textConstraints = constraints.copy(
        minWidth = Integer.max(0, constraints.minWidth - otherWidthsPx.roundToInt()),
        maxWidth = Integer.max(0, constraints.maxWidth - otherWidthsPx.roundToInt())
    )

    return textMeasurer.measure(
        AnnotatedString("00".repeat(NFC_LINE_BYTE_COUNT)),
        textStyle.copy(
            fontSize = textStyle.fontSize * scaleFactor
        ),
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Visible,
        density = LocalDensity.current,
        fontFamilyResolver = LocalFontFamilyResolver.current,
        constraints = textConstraints,
        layoutDirection = LocalLayoutDirection.current
    ).hasVisualOverflow
}
