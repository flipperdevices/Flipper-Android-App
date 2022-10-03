package com.flipperdevices.bridge.api.utils

import org.junit.Assert
import org.junit.Test

class FlipperSymbolFilterTest {

    @Test
    fun `Acceptable letters 1`() {
        val isAcceptable = FlipperSymbolFilter.isAcceptableString("My████key")
        Assert.assertFalse(isAcceptable)
    }

    @Test
    fun `Acceptable letters 2`() {
        val isAcceptable = FlipperSymbolFilter.isAcceptableString("My key")
        Assert.assertTrue(isAcceptable)
    }

    @Test
    fun `Acceptable letters 3`() {
        val isAcceptable = FlipperSymbolFilter.isAcceptableString("My_key")
        Assert.assertTrue(isAcceptable)
    }

    @Test
    fun `Acceptable letters 4`() {
        val isAcceptable = FlipperSymbolFilter.isAcceptableString("My.key")
        Assert.assertFalse(isAcceptable)
    }

    @Test
    fun `Filter letters 1`() {
        val acceptable = FlipperSymbolFilter.filterUnacceptableSymbol("My████key")
        Assert.assertEquals(acceptable, "Mykey")
    }

    @Test
    fun `Filter letters 2`() {
        val acceptable = FlipperSymbolFilter.filterUnacceptableSymbol("My key")
        Assert.assertEquals(acceptable, "My key")
    }

    @Test
    fun `Filter letters 3`() {
        val acceptable = FlipperSymbolFilter.filterUnacceptableSymbol("My-key")
        Assert.assertEquals(acceptable, "My-key")
    }

    @Test
    fun `Filter letters 4`() {
        val acceptable = FlipperSymbolFilter.filterUnacceptableSymbol("My.key")
        Assert.assertEquals(acceptable, "Mykey")
    }

    @Test
    fun `Filter letters for file`() {
        val acceptable = FlipperSymbolFilter.filterUnacceptableSymbolInFileName("My.key")
        Assert.assertEquals(acceptable, "My.key")
    }
}
