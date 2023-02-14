package com.flipperdevices.info.impl.model

import android.content.Context
import com.flipperdevices.info.impl.R
import com.flipperdevices.info.impl.model.deviceinfo.FlipperStorageInformation
import com.flipperdevices.info.impl.model.deviceinfo.StorageStats
import com.flipperdevices.info.impl.model.deviceinfo.isExtStorageEnding
import com.flipperdevices.info.impl.model.deviceinfo.isIntStorageEnding
import com.flipperdevices.info.impl.model.deviceinfo.toString
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.FlipperInformationStatus
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FlipperStorageInformationTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = mockk()
    }

    @Test
    fun `int storage ending`() {
        Assert.assertTrue(
            FlipperStorageInformation(
                internalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        total = 0,
                        free = 20 * 1024
                    )
                )
            ).isIntStorageEnding()
        )

        Assert.assertTrue(
            FlipperStorageInformation(
                internalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        total = 0,
                        free = 20 * 1024 - 1
                    )
                )
            ).isIntStorageEnding()
        )

        Assert.assertFalse(
            FlipperStorageInformation(
                internalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        total = 0,
                        free = 20 * 1024 + 1
                    )
                )
            ).isIntStorageEnding()
        )
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
        every { context.getString(R.string.info_device_info_flash_not_found) } returns "Not found"
        val storage = StorageStats.Error
        val text = storage.toString(context)

        Assert.assertEquals(text, "Not found")
    }
}
