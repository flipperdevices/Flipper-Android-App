package com.flipperdevices.nfceditor.impl.composable

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.text.InternalFoundationTextApi
import androidx.compose.foundation.text.TextDelegate
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontLoader
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

const val WIDTH_LINE_INDEX_DP = 21

private const val NFC_LINE_BYTE_COUNT = 16
private const val SCALE_FACTOR_MULTIPLIER = 0.9f

@Composable
fun BoxWithConstraintsScope.calculateScaleFactor(): Float {
    var currentScaleFactor = 1.0f

    // TODO: Improve it to binary search

    if (shouldShrink(currentScaleFactor)) {
        do {
            currentScaleFactor *= SCALE_FACTOR_MULTIPLIER
        } while (shouldShrink(currentScaleFactor))
        return currentScaleFactor
    } else {
        var previousScaleFactor: Float
        do {
            previousScaleFactor = currentScaleFactor
            currentScaleFactor /= SCALE_FACTOR_MULTIPLIER
        } while (!shouldShrink(currentScaleFactor))
        return previousScaleFactor
    }
}

@OptIn(InternalFoundationTextApi::class)
@Composable
private fun BoxWithConstraintsScope.shouldShrink(scaleFactor: Float): Boolean {
    val textStyle = LocalTextStyle.current

    // Represent line without index and paddings
    val textDelegate = TextDelegate(
        AnnotatedString("00".repeat(NFC_LINE_BYTE_COUNT)),
        textStyle.copy(
            fontSize = textStyle.fontSize * scaleFactor
        ),
        maxLines = 1,
        softWrap = true,
        TextOverflow.Clip,
        LocalDensity.current,
        LocalFontLoader.current
    )

    val otherWidthsDpWithoutScaleFactor = WIDTH_LINE_INDEX_DP +
        (PADDING_CELL_DP * NFC_LINE_BYTE_COUNT)

    val otherWidthsPx = with(LocalDensity.current) {
        (otherWidthsDpWithoutScaleFactor * scaleFactor).dp.toPx()
    }

    val textConstraints = constraints.copy(
        minWidth = Integer.max(0, constraints.minWidth - otherWidthsPx.roundToInt()),
        maxWidth = Integer.max(0, constraints.maxWidth - otherWidthsPx.roundToInt())
    )

    val textLayoutResult = textDelegate.layout(
        textConstraints,
        LocalLayoutDirection.current
    )

    return textLayoutResult.hasVisualOverflow
}
