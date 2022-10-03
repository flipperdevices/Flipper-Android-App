package com.flipperdevices.info.impl.helper

import com.flipperdevices.info.impl.model.StorageStateFormatter
import org.junit.Assert
import org.junit.Test

class StorageStateFormatterTest {

    private val formatter = StorageStateFormatter()

    @Test
    fun `Bytes format`() {
        Assert.assertEquals(
            formatter.formatFileSize(-1),
            "0 B"
        )
        Assert.assertEquals(
            formatter.formatFileSize(1023),
            "1023.0 B"
        )
    }

    @Test
    fun `Kibibytes format`() {
        Assert.assertEquals(
            formatter.formatFileSize(1025),
            "1.0 KiB"
        )

        Assert.assertEquals(
            formatter.formatFileSize(2025),
            "1.97 KiB"
        )

        Assert.assertEquals(
            formatter.formatFileSize(1024 * 1024 - 1),
            "1023.99 KiB"
        )
    }

    @Test
    fun `Megabytes format`() {
        Assert.assertEquals(
            formatter.formatFileSize(1024 * 1024),
            "1.0 MiB"
        )

        Assert.assertEquals(
            formatter.formatFileSize(1024 * 1024 * 2),
            "2.0 MiB"
        )

        Assert.assertEquals(
            formatter.formatFileSize(1024 * 1024 * 1024 - 1),
            "1023.99 MiB"
        )
    }

    @Test
    fun `More megabytes format`() {
        Assert.assertEquals(
            formatter.formatFileSize(1024 * 1024 * 1024),
            "1.0 GiB"
        )

        Assert.assertEquals(
            formatter.formatFileSize(1024L * 1024 * 1024 * 2),
            "2.0 GiB"
        )

        Assert.assertEquals(
            formatter.formatFileSize(1024L * 1024 * 1024 * 1024 - 1),
            "1023.99 GiB"
        )
    }
}
