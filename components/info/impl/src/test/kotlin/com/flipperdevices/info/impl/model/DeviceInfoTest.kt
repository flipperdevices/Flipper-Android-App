package com.flipperdevices.info.impl.model

import com.flipperdevices.bridge.api.model.StorageStats
import org.junit.Assert
import org.junit.Test

class DeviceInfoTest {

    @Test
    fun `Int storage ending`() {
        Assert.assertTrue(
            DeviceInfo(
                flashInt = StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1000
                )
            ).isIntStorageEnding()
        )

        Assert.assertTrue(
            DeviceInfo(
                flashInt =
                StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1000 - 1
                )
            ).isIntStorageEnding()
        )

        Assert.assertFalse(
            DeviceInfo(
                flashInt =
                StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1000 + 1
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
                    free = 20 * 1000
                )
            ).isIntStorageEnding()
        )

        Assert.assertTrue(
            DeviceInfo(
                flashSd =
                StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1000 - 1
                )
            ).isIntStorageEnding()
        )

        Assert.assertFalse(
            DeviceInfo(
                flashSd =
                StorageStats.Loaded(
                    total = 0,
                    free = 20 * 1000 + 1
                )
            ).isIntStorageEnding()
        )
    }
}
