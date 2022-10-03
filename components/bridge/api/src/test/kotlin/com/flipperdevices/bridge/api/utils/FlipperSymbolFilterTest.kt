package com.flipperdevices.bridge.api.utils

import org.junit.Assert
import org.junit.Test

class FlipperSymbolFilterTest {

    private val allowChars = "Hack your Tesla"
    private val notAllowChars = "/"

    @Test
    fun `Check acceptable letters`() {
        val isAcceptable = FlipperSymbolFilter.isAcceptableString(allowChars)
        Assert.assertTrue(isAcceptable)
    }

    @Test
    fun `Check not acceptable letters`() {
        val isAcceptable = FlipperSymbolFilter.isAcceptableString("$allowChars$notAllowChars")
        Assert.assertFalse(isAcceptable)
    }

    @Test
    fun `Filter acceptable letters`() {
        val filter = FlipperSymbolFilter.filterUnacceptableSymbol(allowChars)
        Assert.assertEquals(allowChars, filter)
    }

    @Test
    fun `Filter not acceptable letters`() {
        val filter = FlipperSymbolFilter.filterUnacceptableSymbol("$allowChars$notAllowChars")
        Assert.assertNotEquals("$allowChars$notAllowChars", filter)
    }
}
