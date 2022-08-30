package com.flipperdevices.updater.card.helpers

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.utils.Constants
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
import org.junit.Before
import org.junit.Test

class UpdateOfferHelperTest {
    private val serviceApi: FlipperServiceApi = mockk()
    private val dataStoreSettings: DataStore<Settings> = mockk()
    private val fileExistHelper: FileExistHelper = mockk()
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper = mockk()
    private val updateOfferHelper = UpdateOfferHelper(
        dataStoreSettings = dataStoreSettings,
        fileExistHelper = fileExistHelper,
        subGhzProvisioningHelper = subGhzProvisioningHelper
    )

    @Before
    fun setup() {
        every { serviceApi.requestApi } returns mockk()
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.MANIFEST_FILE,
                requestApi = serviceApi.requestApi
            )
        } returns flowOf(true)
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.REGION_FILE,
                requestApi = serviceApi.requestApi
            )
        } returns flowOf(true)
        every { dataStoreSettings.data } returns flowOf(
            settings {
                alwaysUpdate = false
                lastProvidedRegion = "UA"
            }
        )
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
    }

    @Test
    fun `Not exist manifest on flipper`() = runTest {
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.MANIFEST_FILE,
                requestApi = serviceApi.requestApi
            )
        } returns flowOf(false)
        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Not exist provisioning file subghz on flipper`() = runTest {
        every {
            fileExistHelper.isFileExist(
                pathToFile = Constants.PATH.REGION_FILE,
                requestApi = serviceApi.requestApi
            )
        } returns flowOf(false)
        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Different region`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            settings {
                alwaysUpdate = false
                lastProvidedRegion = "USA"
            }
        )
        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Always update in settings`() = runTest {
        every { dataStoreSettings.data } returns flowOf(
            settings {
                alwaysUpdate = true
            }
        )
        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `No update`() = runTest {
        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
            Assert.assertFalse(it)
        }
    }
}
