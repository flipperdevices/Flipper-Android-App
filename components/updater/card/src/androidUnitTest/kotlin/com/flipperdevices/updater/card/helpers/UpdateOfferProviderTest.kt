package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.api.manager.delegates.FlipperConnectionInformationApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.service.api.FlipperServiceApi
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
        val connectionInformationApi: FlipperConnectionInformationApi = mockk {
            every { getConnectionStateFlow() } returns
                flowOf(ConnectionState.Ready(supportedState = FlipperSupportedState.READY))
        }
        every { serviceApi.connectionInformationApi } returns connectionInformationApi
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
}
