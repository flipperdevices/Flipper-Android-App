package com.flipperdevices.core.ui.composable

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.flipperdevices.core.ui.R

private const val ANNOTATED_STRING_TAG_URL = "URL"

@Composable
fun AnnotatedString.Builder.appendUrl(text: String, url: String = text) {
    val startIndex = length
    val endIndex = startIndex + text.length
    append(text)
    addStyle(
        style = SpanStyle(
            color = colorResource(R.color.accent_secondary),
            textDecoration = TextDecoration.Underline
        ),
        start = startIndex,
        end = endIndex
    )

    addStringAnnotation(
        tag = ANNOTATED_STRING_TAG_URL,
        annotation = url,
        start = startIndex,
        end = endIndex
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
