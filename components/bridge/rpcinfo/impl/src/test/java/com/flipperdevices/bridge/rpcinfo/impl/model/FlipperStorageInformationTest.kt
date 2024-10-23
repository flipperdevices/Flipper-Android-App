package com.flipperdevices.bridge.rpcinfo.impl.model

import android.content.Context
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.rpcinfo.model.StorageStats
import com.flipperdevices.bridge.rpcinfo.model.isExtStorageEnding
import com.flipperdevices.bridge.rpcinfo.model.toString
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import com.flipperdevices.bridge.rpcinfo.api.R as RpcInfoR

class FlipperStorageInformationTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = mockk()
    }

    @Test
    fun `ext storage ending`() {
        Assert.assertTrue(
            FlipperStorageInformation(
                externalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        total = 0,
                        free = 20 * 1024
                    )
                )
            ).isExtStorageEnding()
        )

        Assert.assertTrue(
            FlipperStorageInformation(
                externalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        total = 0,
                        free = 20 * 1024 - 1
                    )
                )
            ).isExtStorageEnding()
        )

        Assert.assertFalse(
            FlipperStorageInformation(
                externalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        total = 0,
                        free = 20 * 1024 + 1
                    )
                )
            ).isExtStorageEnding()
        )
    }

    @Test
    fun `Humanized storage stat not found`() {
        every { context.getString(RpcInfoR.string.info_device_info_flash_not_found) } returns "Not found"
        val storage = StorageStats.Error
        val text = storage.toString(context)

        Assert.assertEquals(text, "Not found")
    }
}
