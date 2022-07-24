package com.flipperdevices.core.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.models.FlipperPallet
import com.flipperdevices.core.ui.theme.models.FlipperTypography
import com.programistich.markdown.model.MarkdownColors
import com.programistich.markdown.model.MarkdownPadding
import com.programistich.markdown.model.MarkdownTypography

@Composable
fun ComposableMarkdown(content: String, modifier: Modifier) {
//    ComposeMarkdown(
//        content = content,
//        colors = LocalPallet.current.toMarkdownColor(),
//        typography = LocalTypography.current.toMarkdownTypography(),
//        padding = markdownPadding,
//        modifier = modifier
//    )
}

@Composable
private fun FlipperPallet.toMarkdownColor(): MarkdownColors {
    return object : MarkdownColors {
        override val backgroundCode: Color
            get() = text20
        override val text: Color
            get() = text100
        override val url: Color
            get() = accentSecond
    }
}

@Composable
private fun FlipperTypography.toMarkdownTypography(): MarkdownTypography {
    return object : MarkdownTypography {
        override val bullet: TextStyle
            get() = bodyR14
        override val code: TextStyle
            get() = bodyR14
        override val h1: TextStyle
            get() = bodySB14
        override val h2: TextStyle
            get() = bodySB14
        override val h3: TextStyle
            get() = bodySB14
        override val h4: TextStyle
            get() = bodySB14
        override val h5: TextStyle
            get() = bodySB14
        override val h6: TextStyle
            get() = bodySB14
        override val list: TextStyle
            get() = bodyR14
        override val ordered: TextStyle
            get() = bodyR14
        override val paragraph: TextStyle
            get() = bodyR14
        override val quote: TextStyle
            get() = bodyR14
        override val text: TextStyle
            get() = bodyR14
    }
}

private val markdownPadding = object : MarkdownPadding {
    override val block: Dp
        get() = 2.dp
    override val list: Dp
        get() = 1.dp
    override val indentList: Dp
        get() = 4.dp
}
