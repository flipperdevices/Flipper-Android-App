package com.flipperdevices.newfilemanager.impl.composable.bar

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ComposableEllipsizeStartText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        var fittableText = text
        var alreadyReplacedCount = 2
        while (shouldShrink(text = fittableText, textStyle = textStyle)) {
            alreadyReplacedCount++
            val remainingLength = text.length - alreadyReplacedCount
            if (remainingLength <= 1) {
                break
            }
            fittableText = "...${text.takeLast(remainingLength)}"
        }

        Text(
            text = fittableText,
            style = textStyle,
            maxLines = 1
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.shouldShrink(
    text: String,
    textStyle: TextStyle
): Boolean {
    val textMeasurer = rememberTextMeasurer()

    // Represent line without index and paddings
    val textLayoutResult = textMeasurer.measure(
        AnnotatedString(text),
        textStyle,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Visible,
        density = LocalDensity.current,
        fontFamilyResolver = LocalFontFamilyResolver.current,
        constraints = constraints,
        layoutDirection = LocalLayoutDirection.current
    )

    return textLayoutResult.hasVisualOverflow
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
private fun ComposableEllipsizeStartTextPreview() {
    Column {
        ComposableEllipsizeStartText("Small text")
        ComposableEllipsizeStartText("abc".repeat(n = 50))
    }
}
