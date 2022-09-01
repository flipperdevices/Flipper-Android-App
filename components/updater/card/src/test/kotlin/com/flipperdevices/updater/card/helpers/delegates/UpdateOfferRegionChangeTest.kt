package com.flipperdevices.updater.card.helpers.delegates

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.preference.pb.settings
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class UpdateOfferRegionChangeTest {
    private val serviceApi: FlipperServiceApi = mockk()
    private val dataStoreSettings: DataStore<Settings> = mockk()
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper = mockk()
    private val delegate: UpdateOfferDelegate = UpdateOfferRegionChange(
        dataStoreSettings = dataStoreSettings,
        subGhzProvisioningHelper = subGhzProvisioningHelper
    )

    @Test
    fun `Same region in storage and now`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            settings {
                lastProvidedRegion = "UA"
            }
        )
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
        delegate.isRequire(serviceApi).collect {
            Assert.assertFalse(it)
        }
    }

    @Test
    fun `Different region in storage and now`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            settings {
                lastProvidedRegion = "RU"
            }
        )
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
        delegate.isRequire(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Exception when we get current region`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            settings {
                lastProvidedRegion = "UA"
            }
        )
        coEvery { subGhzProvisioningHelper.getRegion() }.throws(Exception("Some error"))
        delegate.isRequire(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }
}
