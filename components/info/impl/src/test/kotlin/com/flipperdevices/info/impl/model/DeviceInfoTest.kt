package com.flipperdevices.info.impl.model

import android.content.Context
import com.flipperdevices.bridge.api.model.StorageStats
import com.flipperdevices.info.impl.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DeviceInfoTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = mockk()
    }

    @Test
    fun `Int storage ending`() {
        Assert.assertTrue(
            DeviceInfo(
                flashInt = StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1024
                )
            ).isIntStorageEnding()
        )

        Assert.assertTrue(
            DeviceInfo(
                flashInt =
                StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1024 - 1
                )
            ).isIntStorageEnding()
        )

        Assert.assertFalse(
            DeviceInfo(
                flashInt =
                StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1024 + 1
                )
            ).isIntStorageEnding()
        )
    }

    @Test
    fun `Ext storage ending`() {
        Assert.assertTrue(
            DeviceInfo(
                flashSd = StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1024
                )
            ).isExtStorageEnding()
        )

        Assert.assertTrue(
            DeviceInfo(
                flashSd =
                StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1024 - 1
                )
            ).isExtStorageEnding()
        )

        Assert.assertFalse(
            DeviceInfo(
                flashSd =
                StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1024 + 1
                )
            ).isExtStorageEnding()
        )
    }

    @Test
    fun `Humanized storage stat not found`() {
        every { context.getString(R.string.info_device_info_flash_not_found) } returns "Not found"
        val storage = StorageStats.Error
        val text = storage.toString(context)

        Assert.assertEquals(text, "Not found")
    }
}
