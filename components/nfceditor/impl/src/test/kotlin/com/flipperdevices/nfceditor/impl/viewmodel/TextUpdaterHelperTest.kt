package com.flipperdevices.nfceditor.impl.viewmodel

import org.junit.Assert
import org.junit.Test

class TextUpdaterHelperTest {
    private val underTest: TextUpdaterHelper = TextUpdaterHelper()

    @Test
    fun `delete symbol from 1 index`() {
        val resultText = underTest.getProcessedText(
            originalText = "0F",
            newText = "F",
            oldPosition = 1,
            newPosition = 0
        )

        Assert.assertEquals("?F", resultText)
    }

    @Test
    fun `delete symbol from 1 index with same symbol`() {
        val resultText = underTest.getProcessedText(
            originalText = "00",
            newText = "0",
            oldPosition = 1,
            newPosition = 0
        )

        Assert.assertEquals("?0", resultText)
    }

    @Test
    fun `delete symbol from 1 index to empty cell`() {
        val resultText = underTest.getProcessedText(
            originalText = "F?",
            newText = "?",
            oldPosition = 1,
            newPosition = 0
        )

        Assert.assertEquals("??", resultText)
    }

    @Test
    fun `delete symbol from 2 index`() {
        val resultText = underTest.getProcessedText(
            originalText = "0F",
            newText = "0",
            oldPosition = 2,
            newPosition = 1
        )

        Assert.assertEquals("0?", resultText)
    }

    @Test
    fun `delete symbol from 2 index with same symbol`() {
        val resultText = underTest.getProcessedText(
            originalText = "00",
            newText = "0",
            oldPosition = 2,
            newPosition = 1
        )

        Assert.assertEquals("0?", resultText)
    }

    @Test
    fun `add symbol from 0 index on existed text`() {
        val resultText = underTest.getProcessedText(
            originalText = "0A",
            newText = "F0A",
            oldPosition = 0,
            newPosition = 1
        )

        Assert.assertEquals("FA", resultText)
    }

    @Test
    fun `add symbol from 0 index on empty cell`() {
        val resultText = underTest.getProcessedText(
            originalText = "?A",
            newText = "F0A",
            oldPosition = 0,
            newPosition = 1
        )

        Assert.assertEquals("FA", resultText)
    }

    @Test
    fun `add symbol from 1 index on existed text`() {
        val resultText = underTest.getProcessedText(
            originalText = "0A",
            newText = "0FA",
            oldPosition = 1,
            newPosition = 2
        )

        Assert.assertEquals("0F", resultText)
    }

    @Test
    fun `add symbol from 0 index two symbol`() {
        val resultText = underTest.getProcessedText(
            originalText = "0A",
            newText = "BF0A",
            oldPosition = 0,
            newPosition = 2
        )

        Assert.assertEquals("BF", resultText)
    }

    @Test
    fun `remove symbol from 2 index two symbol`() {
        val resultText = underTest.getProcessedText(
            originalText = "0A",
            newText = "",
            oldPosition = 2,
            newPosition = 0
        )

        Assert.assertEquals("??", resultText)
    }
}
