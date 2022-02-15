package com.flipperdevices.core.markdown

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.vladsch.flexmark.parser.Parser

@Composable
fun annotatedStringFromMarkdown(markdown: String): AnnotatedString {
    val parser = remember { Parser.builder().build() }
    return remember(markdown) {
        return@remember AnnotatedStringRenderer.render(parser.parse(markdown))
    }
}

@Composable
fun annotatedStringFromMarkdown(@StringRes markdownResId: Int): AnnotatedString {
    return annotatedStringFromMarkdown(stringResource(markdownResId))
}
