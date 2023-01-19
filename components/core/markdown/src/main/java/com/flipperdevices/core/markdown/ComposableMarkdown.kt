package com.flipperdevices.core.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownPadding
import com.mikepenz.markdown.model.MarkdownTypography

@Composable
fun ComposableMarkdown(
    content: String,
    modifier: Modifier = Modifier,
    typography: MarkdownTypography = markdownTypography(),
    colors: MarkdownColors = markdownColors(),
    paddings: MarkdownPadding = markdownPadding()
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
    backgroundCode: Color = LocalPallet.current.text20,
    text: Color = LocalPallet.current.text100,
    link: Color = LocalPallet.current.accentSecond
): MarkdownColors {
    return object : MarkdownColors {
        override val backgroundCode = backgroundCode
        override val text = text
        override val markdownLink: Color = link
        override val autoLink: Color = link
    }
}

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
    listStyle: TextStyle = LocalTypography.current.bodyR14,
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
        override val list = listStyle.merge(additionalTextStyle)
        override val ordered = orderedStyle.merge(additionalTextStyle)
        override val paragraph = paragraphStyle.merge(additionalTextStyle)
        override val quote = quoteStyle.merge(additionalTextStyle)
        override val text = textStyle.merge(additionalTextStyle)
    }
}

@Composable
fun markdownPadding(
    block: Dp = 2.dp,
    indentList: Dp = 4.dp,
    list: Dp = 1.dp
): MarkdownPadding {
    return object : MarkdownPadding {
        override val block: Dp = block
        override val indentList: Dp = indentList
        override val list: Dp = list
    }
}
