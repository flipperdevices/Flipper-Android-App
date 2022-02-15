package com.flipperdevices.core.markdown

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.vladsch.flexmark.parser.Parser
import org.junit.Assert.assertEquals
import org.junit.Test

class AnnotatedStringGeneratorTest {
    @Test
    fun `italic markdown`() {
        val parser = Parser.builder().build()
        val src = "Italic *Markdown*"
        val document = parser.parse(src)
        val annotatedString = AnnotatedStringRenderer.render(document)

        assertEquals(1, annotatedString.spanStyles.size)
        assertEquals(FontStyle.Italic, annotatedString.spanStyles.first().item.fontStyle)
    }

    @Test
    fun `bold markdown`() {
        val parser = Parser.builder().build()
        val src = "Bold **Markdown**"
        val document = parser.parse(src)
        val annotatedString = AnnotatedStringRenderer.render(document)

        assertEquals(1, annotatedString.spanStyles.size)
        assertEquals(FontWeight.Bold, annotatedString.spanStyles.first().item.fontWeight)
    }
}
