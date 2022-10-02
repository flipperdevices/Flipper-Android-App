package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.region
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.readRequest
import com.flipperdevices.protobuf.storage.readResponse
import com.flipperdevices.protobuf.system.pingRequest
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import com.google.protobuf.ByteString
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.nio.charset.Charset
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UpdateOfferRegionChangeTest {
    private val request = main {
        storageReadRequest = readRequest {
            path = Constants.PATH.REGION_FILE
        }
    }.wrapToRequest(FlipperRequestPriority.BACKGROUND)

    private fun region(code: String) = region {
        countryCode = ByteString.copyFrom(
            code,
            Charset.forName("ASCII")
        )
        bands.addAll(listOf())
    }.toByteString()

    private fun getResponse(region: ByteString): Flipper.Main {
        return main {
            commandStatus = Flipper.CommandStatus.OK
            storageReadResponse = readResponse {
                file = file { data = region }
            }
        }
    }

    private val requestApi: FlipperRequestApi = mockk()
    private val serviceApi: FlipperServiceApi = mockk()
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper = mockk()
    private val delegate: UpdateOfferDelegate = UpdateOfferRegionChange(
        subGhzProvisioningHelper = subGhzProvisioningHelper
    )

    @Before
    fun setup() {
        every { serviceApi.requestApi } returns requestApi
    }

    @Test
    fun `Same region in storage and now`() = runTest {
        every { requestApi.request(request) } answers {
            flowOf(getResponse(region("UA")))
        }
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
        delegate.isRequire(serviceApi).collect { Assert.assertFalse(it) }
    }

    @Test
    fun `Different region in storage and now`() = runTest {
        every { requestApi.request(request) } answers {
            flowOf(getResponse(region("USA")))
        }
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
        delegate.isRequire(serviceApi).collect { Assert.assertTrue(it) }
    }

    @Test
    fun `Exception when we get current region`() = runTest {
        every { requestApi.request(request) } answers {
            flowOf(getResponse(region("USA")))
        }
        coEvery { subGhzProvisioningHelper.getRegion() }.throws(Exception("Some error"))
        delegate.isRequire(serviceApi).collect { Assert.assertTrue(it) }
    }

    @Test
    fun `Exception when we get region from flipper`() = runTest {
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
        every { serviceApi.requestApi.request(request) } answers {
            flowOf(
                main {
                    commandStatus = Flipper.CommandStatus.ERROR
                }
            )
        }
        delegate.isRequire(serviceApi).collect { Assert.assertTrue(it) }

        every { serviceApi.requestApi.request(request) } answers {
            flowOf(
                main {
                    commandStatus = Flipper.CommandStatus.OK
                    pingRequest { }
                }
            )
        }
        delegate.isRequire(serviceApi).collect { Assert.assertTrue(it) }
    }
}
