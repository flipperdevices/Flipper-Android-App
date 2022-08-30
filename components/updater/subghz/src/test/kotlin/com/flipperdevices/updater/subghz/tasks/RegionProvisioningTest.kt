package com.flipperdevices.updater.subghz.tasks

import com.flipperdevices.updater.subghz.helpers.model.RegionProvisioning
import com.flipperdevices.updater.subghz.helpers.model.RegionProvisioningSource
import org.junit.Assert
import org.junit.Test

class RegionProvisioningTest {
    @Test
    fun `network country win`() {
        val actualRegion = RegionProvisioning(
            regionFromNetwork = "W1",
            regionFromSim = "W2",
            regionFromIp = "W3",
            regionSystem = "W4",
            isRoaming = false
        )

        Assert.assertEquals(
            "W1" to RegionProvisioningSource.SIM_NETWORK,
            actualRegion.provideRegion()
        )
    }

    @Test
    fun `sim country win`() {
        val actualRegion = RegionProvisioning(
            regionFromNetwork = null,
            regionFromSim = "W2",
            regionFromIp = "W3",
            regionSystem = "W4",
            isRoaming = false
        )

        Assert.assertEquals(
            "W2" to RegionProvisioningSource.SIM_COUNTRY,
            actualRegion.provideRegion()
        )
    }

    @Test
    fun `ip country win`() {
        val actualRegion = RegionProvisioning(
            regionFromNetwork = null,
            regionFromSim = null,
            regionFromIp = "W3",
            regionSystem = "W4",
            isRoaming = false
        )

        Assert.assertEquals(
            "W3" to RegionProvisioningSource.GEO_IP,
            actualRegion.provideRegion()
        )
    }

    @Test
    fun `system country win`() {
        val actualRegion = RegionProvisioning(
            regionFromNetwork = null,
            regionFromSim = null,
            regionFromIp = null,
            regionSystem = "W4",
            isRoaming = false
        )

        Assert.assertEquals(
            "W4" to RegionProvisioningSource.SYSTEM,
            actualRegion.provideRegion()
        )
    }

    @Test
    fun `wrong country length big country`() {
        val actualRegion = RegionProvisioning(
            regionFromNetwork = "WWW",
            regionFromSim = "W2",
            regionFromIp = "W3",
            regionSystem = "W4",
            isRoaming = false
        )

        Assert.assertEquals(
            "W2" to RegionProvisioningSource.SIM_COUNTRY,
            actualRegion.provideRegion()
        )
    }

    @Test
    fun `wrong country length small country`() {
        val actualRegion = RegionProvisioning(
            regionFromNetwork = "W",
            regionFromSim = "W2",
            regionFromIp = "W3",
            regionSystem = "W4",
            isRoaming = false
        )

        Assert.assertEquals(
            "W2" to RegionProvisioningSource.SIM_COUNTRY,
            actualRegion.provideRegion()
        )
    }

    @Test
    fun `wrong country empty country`() {
        val actualRegion = RegionProvisioning(
            regionFromNetwork = "",
            regionFromSim = "W2",
            regionFromIp = "W3",
            regionSystem = "W4",
            isRoaming = false
        )

        Assert.assertEquals(
            "W2" to RegionProvisioningSource.SIM_COUNTRY,
            actualRegion.provideRegion()
        )
    }
}
