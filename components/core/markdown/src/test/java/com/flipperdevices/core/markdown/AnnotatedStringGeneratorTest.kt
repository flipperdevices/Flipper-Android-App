package com.flipperdevices.core.markdown

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.vladsch.flexmark.parser.Parser
import org.junit.Assert.assertEquals
import org.junit.Test

class AnnotatedStringGeneratorTest {
    private val underTest = AnnotatedStringRenderer(
        linkColor = Color.Blue
    )

    @Test
    fun `italic markdown`() {
        val parser = Parser.builder().build()
        val src = "Italic *Markdown*"
        val document = parser.parse(src)
        val annotatedString = underTest.render(document)

        assertEquals(1, annotatedString.spanStyles.size)
        assertEquals(FontStyle.Italic, annotatedString.spanStyles.first().item.fontStyle)
    }

    @Test
    fun `bold markdown`() {
        val parser = Parser.builder().build()
        val src = "Bold **Markdown**"
        val document = parser.parse(src)
        val annotatedString = underTest.render(document)

        assertEquals(1, annotatedString.spanStyles.size)
        assertEquals(FontWeight.Bold, annotatedString.spanStyles.first().item.fontWeight)
    }

    @Test
    fun `link markdown`() {
        val parser = Parser.builder().build()
        val src = "Link to [Markdown](https://google.com)"
        val document = parser.parse(src)
        val annotatedString = underTest.render(document)

        assertEquals(1, annotatedString.spanStyles.size)
        val spanStyle = annotatedString.spanStyles.first()
        assertEquals(
            TextDecoration.Underline,
            annotatedString.spanStyles.first().item.textDecoration
        )
        assertEquals(
            Color.Blue,
            annotatedString.spanStyles.first().item.color
        )
        assertEquals(
            "Markdown",
            annotatedString.subSequence(spanStyle.start, spanStyle.end).toString()
        )

        val linkAnnotation = annotatedString.getLinkAnnotations(
            0,
            annotatedString.length
        )
        assertEquals(1, linkAnnotation.size)
        assertEquals("https://google.com", (linkAnnotation.first().item as LinkAnnotation.Url).url)
    }
}
