package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferDebugFlagAlways
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferFlipperManifest
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferFlipperRegionFile
import com.flipperdevices.updater.card.helpers.delegates.UpdateOfferRegionChange
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UpdateOfferProviderTest {
    private val delegateRegion: UpdateOfferRegionChange = mockk()
    private val delegateFlagAlways: UpdateOfferDebugFlagAlways = mockk()
    private val delegateManifest: UpdateOfferFlipperManifest = mockk()
    private val delegateRegionFile: UpdateOfferFlipperRegionFile = mockk()
    private val fStorageFeatureApi: FStorageFeatureApi = mockk()
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
        every { delegateRegion.isRequire(fStorageFeatureApi) } returns flowOf(false)
        every { delegateFlagAlways.isRequire(fStorageFeatureApi) } returns flowOf(false)
        every { delegateManifest.isRequire(fStorageFeatureApi) } returns flowOf(false)
        every { delegateRegionFile.isRequire(fStorageFeatureApi) } returns flowOf(false)
    }

    @Test
    fun `Update not offer`() = runTest {
        updateOfferHelper.isUpdateRequire(fStorageFeatureApi).collect {
            Assert.assertFalse(it)
        }
    }

    @Test
    fun `Region changes`() = runTest {
        every { delegateRegion.isRequire(fStorageFeatureApi) } returns flowOf(true)
        updateOfferHelper.isUpdateRequire(fStorageFeatureApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Always update in setting`() = runTest {
        every { delegateFlagAlways.isRequire(fStorageFeatureApi) } returns flowOf(true)
        updateOfferHelper.isUpdateRequire(fStorageFeatureApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Manifest file not exist`() = runTest {
        every { delegateManifest.isRequire(fStorageFeatureApi) } returns flowOf(true)
        updateOfferHelper.isUpdateRequire(fStorageFeatureApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Region file not exist`() = runTest {
        every { delegateRegion.isRequire(fStorageFeatureApi) } returns flowOf(true)
        updateOfferHelper.isUpdateRequire(fStorageFeatureApi).collect {
            Assert.assertTrue(it)
        }
    }
}
