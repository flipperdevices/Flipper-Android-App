package com.flipperdevices.core.ktx.jre

import org.junit.Assert
import org.junit.Test

class StorageStateFormatterTest {

    @Test
    fun `Bytes format`() {
        Assert.assertEquals(
            (-1).toFormattedSize(),
            "0 B"
        )
        Assert.assertEquals(
            1023.toFormattedSize(),
            "1023.0 B"
        )

        Assert.assertEquals(
            300.toFormattedSize(),
            "300.0 B"
        )
    }

    @Test
    fun `Kibibytes format`() {
        Assert.assertEquals(
            1025.toFormattedSize(),
            "1.0 KiB"
        )

        Assert.assertEquals(
            1100.toFormattedSize(),
            "1.07 KiB"
        )

        Assert.assertEquals(
            2025.toFormattedSize(),
            "1.97 KiB"
        )

        Assert.assertEquals(
            (1024 * 1024 - 1).toFormattedSize(),
            "1023.99 KiB"
        )
    }

    @Test
    fun `Megabytes format`() {
        Assert.assertEquals(
            (1024 * 1024).toFormattedSize(),
            "1.0 MiB"
        )

        Assert.assertEquals(
            (1024 * 1024 * 2).toFormattedSize(),
            "2.0 MiB"
        )

        Assert.assertEquals(
            (1024 * 1024 * 1024 - 1).toFormattedSize(),
            "1023.99 MiB"
        )
    }

    @Test
    fun `More megabytes format`() {
        Assert.assertEquals(
            (1024 * 1024 * 1024).toFormattedSize(),
            "1.0 GiB"
        )

        Assert.assertEquals(
            (1024L * 1024 * 1024 * 2).toFormattedSize(),
            "2.0 GiB"
        )

        Assert.assertEquals(
            (1024L * 1024 * 1024 * 1024 - 1).toFormattedSize(),
            "1023.99 GiB"
        )
    }
}
