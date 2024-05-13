package com.flipperdevices.updater.impl

import com.flipperdevices.updater.impl.api.FirmwareVersionBuilderApiImpl
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class FirmwareVersionBuildHelperTest(
    private val version: String,
    private val firmwareVersion: FirmwareVersion?
) {

    @Test
    fun `Correct firmware`() {
        val firmware = FirmwareVersionBuilderApiImpl().buildFirmwareVersionFromString(version)
        Assert.assertEquals(firmware, firmwareVersion)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(
                "fc15d5cc 0.61.1 1333 28-06-2022",
                FirmwareVersion(
                    channel = FirmwareChannel.RELEASE,
                    version = "0.61.1",
                    buildDate = "28-06-2022"
                )
            ),
            arrayOf(
                "577a4ba5 0.62.1-rc 1327 12-07-2022",
                FirmwareVersion(
                    channel = FirmwareChannel.RELEASE_CANDIDATE,
                    version = "0.62.1",
                    buildDate = "12-07-2022"
                )
            ),
            arrayOf(
                "2a8ea142 dev 2270 11-07-2022",
                FirmwareVersion(
                    channel = FirmwareChannel.DEV,
                    version = "2a8ea142",
                    buildDate = "11-07-2022"
                )
            ),
            arrayOf(
                "532082f3 dev-cfw 1784 10-07-2022",
                FirmwareVersion(
                    channel = FirmwareChannel.UNKNOWN,
                    version = "dev-cfw",
                    buildDate = "10-07-2022"
                )
            ),
            arrayOf(
                "532082f3 1784 10-07-2022",
                FirmwareVersion(
                    channel = FirmwareChannel.UNKNOWN,
                    version = "532082f3 1784 10-07-2022",
                    buildDate = null
                )
            )
        )
    }
}
