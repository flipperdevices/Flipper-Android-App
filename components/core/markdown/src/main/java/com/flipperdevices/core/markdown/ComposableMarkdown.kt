package com.flipperdevices.core.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
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
    inlineCodeText = text,
    tableText = text,
    tableBackground = backgroundCode,
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
    return DefaultMarkdownTypography(
        bullet = bulletStyle.merge(additionalTextStyle),
        code = codeStyle.merge(additionalTextStyle),
        h1 = h1Style.merge(additionalTextStyle),
        h2 = h2Style.merge(additionalTextStyle),
        h3 = h3Style.merge(additionalTextStyle),
        h4 = h4Style.merge(additionalTextStyle),
        h5 = h5Style.merge(additionalTextStyle),
        h6 = h6Style.merge(additionalTextStyle),
        inlineCode = inlineCode.merge(additionalTextStyle),
        link = linkStyle.merge(additionalTextStyle),
        list = listStyle.merge(additionalTextStyle),
        ordered = orderedStyle.merge(additionalTextStyle),
        paragraph = paragraphStyle.merge(additionalTextStyle),
        quote = quoteStyle.merge(additionalTextStyle),
        text = textStyle.merge(additionalTextStyle),
        textLink = TextLinkStyles(
            style = textStyle.merge(additionalTextStyle).copy(
                textDecoration = TextDecoration.Underline,
            ).toSpanStyle()
        ),
        table = textStyle.merge(additionalTextStyle)
    )
}
