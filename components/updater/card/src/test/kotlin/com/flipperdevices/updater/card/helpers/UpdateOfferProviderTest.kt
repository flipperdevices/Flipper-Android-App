package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegateFlagAlways
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegateFlipperManifest
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegateFlipperRegionFile
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDelegateRegionChange
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UpdateOfferProviderTest {
    private val delegateRegion: UpdateOfferDelegateRegionChange = mockk()
    private val delegateFlagAlways: UpdateOfferDelegateFlagAlways = mockk()
    private val delegateManifest: UpdateOfferDelegateFlipperManifest = mockk()
    private val delegateRegionFile: UpdateOfferDelegateFlipperRegionFile = mockk()
    private val serviceApi: FlipperServiceApi = mockk()
    private val updateOfferHelper = UpdateOfferProvider(
        delegates = mutableSetOf(
            delegateRegion,
            delegateFlagAlways,
            delegateManifest,
            delegateRegionFile
        )
    )

    @Before
    fun setup() {
        every { delegateRegion.isRequire(serviceApi) } returns flowOf(false)
        every { delegateFlagAlways.isRequire(serviceApi) } returns flowOf(false)
        every { delegateManifest.isRequire(serviceApi) } returns flowOf(false)
        every { delegateRegionFile.isRequire(serviceApi) } returns flowOf(false)
    }

    @Test
    fun `Update not offer`() = runTest {
        updateOfferHelper.isUpdateRequire(serviceApi).collect {
            Assert.assertFalse(it)
        }
    }

    @Test
    fun `Region changes`() = runTest {
        every { delegateRegion.isRequire(serviceApi) } returns flowOf(true)
        updateOfferHelper.isUpdateRequire(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Always update in setting`() = runTest {
        every { delegateFlagAlways.isRequire(serviceApi) } returns flowOf(true)
        updateOfferHelper.isUpdateRequire(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Manifest file not exist`() = runTest {
        every { delegateManifest.isRequire(serviceApi) } returns flowOf(true)
        updateOfferHelper.isUpdateRequire(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Region file not exist`() = runTest {
        every { delegateRegion.isRequire(serviceApi) } returns flowOf(true)
        updateOfferHelper.isUpdateRequire(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

//    @Before
//    fun setup() {
//        every { serviceApi.requestApi } returns mockk()
//        every {
//            fileExistHelper.isFileExist(
//                pathToFile = Constants.PATH.MANIFEST_FILE,
//                requestApi = serviceApi.requestApi
//            )
//        } returns flowOf(true)
//        every {
//            fileExistHelper.isFileExist(
//                pathToFile = Constants.PATH.REGION_FILE,
//                requestApi = serviceApi.requestApi
//            )
//        } returns flowOf(true)
//        every { dataStoreSettings.data } returns flowOf(
//            settings {
//                alwaysUpdate = false
//                lastProvidedRegion = "UA"
//            }
//        )
//        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
//    }

//    @Test
//    fun `Not exist manifest on flipper`() = runTest {
//        every {
//            fileExistHelper.isFileExist(
//                pathToFile = Constants.PATH.MANIFEST_FILE,
//                requestApi = serviceApi.requestApi
//            )
//        } returns flowOf(false)
//        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
//            Assert.assertTrue(it)
//        }
//    }
//
//    @Test
//    fun `Not exist provisioning file subghz on flipper`() = runTest {
//        every {
//            fileExistHelper.isFileExist(
//                pathToFile = Constants.PATH.REGION_FILE,
//                requestApi = serviceApi.requestApi
//            )
//        } returns flowOf(false)
//        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
//            Assert.assertTrue(it)
//        }
//    }
//
//    @Test
//    fun `Different region`() = runTest {
//        every { dataStoreSettings.data } returns flowOf(
//            settings {
//                alwaysUpdate = false
//                lastProvidedRegion = "USA"
//            }
//        )
//        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
//            Assert.assertTrue(it)
//        }
//    }
//
//    @Test
//    fun `Always update in settings`() = runTest {
//        every { dataStoreSettings.data } returns flowOf(
//            settings {
//                alwaysUpdate = true
//            }
//        )
//        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
//            Assert.assertTrue(it)
//        }
//    }
//
//    @Test
//    fun `No update`() = runTest {
//        updateOfferHelper.isAlwaysUpdate(serviceApi).collect {
//            Assert.assertFalse(it)
//        }
//    }
}
