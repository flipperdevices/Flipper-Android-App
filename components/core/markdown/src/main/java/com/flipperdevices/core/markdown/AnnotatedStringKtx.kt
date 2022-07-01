package com.flipperdevices.core.markdown

import androidx.annotation.StringRes
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.flipperdevices.core.ui.theme.LocalPallet
import com.vladsch.flexmark.parser.Parser

@Composable
fun annotatedStringFromMarkdown(markdown: String): AnnotatedString {
    val parser = remember { Parser.builder().build() }
    val renderer = rememberRenderer(linkColor = LocalPallet.current.background)
    return remember(parser, renderer, markdown) {
        renderer.render(parser.parse(markdown))
    }
}

@Composable
fun annotatedStringFromMarkdown(@StringRes markdownResId: Int): AnnotatedString {
    return annotatedStringFromMarkdown(stringResource(markdownResId))
}

@Composable
private fun rememberRenderer(linkColor: Color): AnnotatedStringRenderer {
    return remember(linkColor) { AnnotatedStringRenderer(linkColor) }
}

@Composable
fun ClickableUrlText(
    modifier: Modifier = Modifier,
    @StringRes markdownResId: Int,
    style: TextStyle = TextStyle.Default
) {
    ClickableUrlText(
        annotatedStringFromMarkdown(markdownResId),
        modifier,
        style
    )
}

@Composable
fun ClickableUrlText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default
) {
    val textStyle = LocalTextStyle.current.merge(style)
    val uriHandler = LocalUriHandler.current
    ClickableText(
        modifier = modifier,
        text = text,
        style = textStyle,
        onClick = { index ->
            text.getStringAnnotations(ANNOTATED_STRING_TAG_URL, index, index)
                .firstOrNull()?.let { url -> uriHandler.openUri(url.item) }
        }
    )
}
