package com.flipperdevices.core.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownPadding
import com.mikepenz.markdown.model.MarkdownTypography
import com.mikepenz.markdown.model.markdownPadding

@Composable
fun ComposableMarkdown(
    content: String,
    modifier: Modifier = Modifier,
    typography: MarkdownTypography = markdownTypography(),
    colors: MarkdownColors = markdownColors(),
    paddings: MarkdownPadding = markdownPadding(
        block = 2.dp,
        indentList = 4.dp,
        list = 1.dp
    )
) {
    Markdown(
        content = content,
        colors = colors,
        typography = typography,
        padding = paddings,
        modifier = modifier
    )
}

@Composable
fun markdownColors(
    backgroundCode: Color = LocalPallet.current.text8,
    text: Color = LocalPallet.current.text100,
    link: Color = LocalPallet.current.accentSecond,
    dividerColor: Color = LocalPallet.current.divider12
) = DefaultMarkdownColors(
    text = text,
    codeText = text,
    linkText = link,
    codeBackground = backgroundCode,
    inlineCodeBackground = backgroundCode,
    dividerColor = dividerColor,
    inlineCodeText = text
)

@Composable
fun markdownTypography(
    additionalTextStyle: TextStyle = TextStyle(),
    bulletStyle: TextStyle = LocalTypography.current.bodyR14,
    codeStyle: TextStyle = LocalTypography.current.bodyR14,
    h1Style: TextStyle = LocalTypography.current.bodySB14,
    h2Style: TextStyle = LocalTypography.current.bodySB14,
    h3Style: TextStyle = LocalTypography.current.bodySB14,
    h4Style: TextStyle = LocalTypography.current.bodySB14,
    h5Style: TextStyle = LocalTypography.current.bodySB14,
    h6Style: TextStyle = LocalTypography.current.bodySB14,
    inlineCode: TextStyle = LocalTypography.current.monoSpaceM14,
    listStyle: TextStyle = LocalTypography.current.bodyR14,
    linkStyle: TextStyle = LocalTypography.current.bodyR14.copy(textDecoration = TextDecoration.Underline),
    orderedStyle: TextStyle = LocalTypography.current.bodyR14,
    paragraphStyle: TextStyle = LocalTypography.current.bodyR14,
    quoteStyle: TextStyle = LocalTypography.current.bodyR14,
    textStyle: TextStyle = LocalTypography.current.bodyR14
): MarkdownTypography {
    return object : MarkdownTypography {
        override val bullet = bulletStyle.merge(additionalTextStyle)
        override val code = codeStyle.merge(additionalTextStyle)
        override val h1 = h1Style.merge(additionalTextStyle)
        override val h2 = h2Style.merge(additionalTextStyle)
        override val h3 = h3Style.merge(additionalTextStyle)
        override val h4 = h4Style.merge(additionalTextStyle)
        override val h5 = h5Style.merge(additionalTextStyle)
        override val h6 = h6Style.merge(additionalTextStyle)
        override val inlineCode = inlineCode.merge(additionalTextStyle)
        override val link = linkStyle.merge(additionalTextStyle)
        override val list = listStyle.merge(additionalTextStyle)
        override val ordered = orderedStyle.merge(additionalTextStyle)
        override val paragraph = paragraphStyle.merge(additionalTextStyle)
        override val quote = quoteStyle.merge(additionalTextStyle)
        override val text = textStyle.merge(additionalTextStyle)
    }
}
