package com.flipperdevices.updater.subghz.tasks

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.ktx.jre.flatten
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.test.readTestAsset
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.updater.subghz.helpers.SkipProvisioningHelper
import com.flipperdevices.updater.subghz.helpers.SkipProvisioningHelperImpl
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class SkipProvisioningHelperTest {
    private lateinit var settings: DataStore<Settings>
    private lateinit var underTest: SkipProvisioningHelper

    @Before
    fun setUp() {
        settings = mockk()
        underTest = SkipProvisioningHelperImpl(settings)
    }


    @Test
    fun `check region skip`() = runTest {
        whenever(settings.data).doReturn(
            flowOf(
                Settings.getDefaultInstance().toBuilder()
                    .setIgnoreSubghzProvisioningOnZeroRegion(true)
                    .build()
            )
        )

        val mockRequestApi: FlipperRequestApi = mock()
        val serviceApi: FlipperServiceApi = mock() {
            on { requestApi } doReturn mockRequestApi
        }

        val shouldProvide = underTest.shouldSkipProvisioning(serviceApi)

        verifyNoInteractions(requestApi)
    }

    @Test
    fun `not skip provisioning when hardware region is not zero`() = runTest {
        whenever(settings.data).doReturn(
            flowOf(
                Settings.getDefaultInstance().toBuilder()
                    .setIgnoreSubghzProvisioningOnZeroRegion(true)
                    .build()
            )
        )

        var flipperRequests: List<FlipperRequest>? = null
        val requestApi: FlipperRequestApi = mock()
        val flipperRpcInformationApi: FlipperRpcInformationApi = mock()
        val serviceApi: FlipperServiceApi = mock()
        whenever(serviceApi.flipperRpcInformationApi).doReturn(flipperRpcInformationApi)
        whenever(flipperRpcInformationApi.getRequestRpcInformationStatus()).doReturn(
            MutableStateFlow(
                FlipperRequestRpcInformationStatus.InProgress(
                    rpcDeviceInfoRequestFinished = true
                )
            )
        )
        whenever(flipperRpcInformationApi.getRpcInformationFlow()).doReturn(
            MutableStateFlow(
                FlipperRpcInformation(flipperDeviceInfo = FlipperDeviceInfo(hardwareRegion = "4"))
            )
        )
        whenever(serviceApi.requestApi).doReturn(requestApi)
        whenever(requestApi.request(any(), any())).doAnswer {
            flipperRequests = runBlocking {
                (it.arguments.first() as Flow<*>)
                    .filterIsInstance<FlipperRequest>()
                    .toList()
            }
            return@doAnswer main {
                commandStatus = Flipper.CommandStatus.OK
            }
        }

        underTest.provideAndUploadSubGhz(serviceApi)

        Assert.assertNotNull(flipperRequests)
        Assert.assertTrue(flipperRequests!!.isNotEmpty())
        val notNullableFlipperRequest = flipperRequests!!
        notNullableFlipperRequest.forEach {
            Assert.assertEquals("/int/.region_data", it.data.storageWriteRequest.path)
        }
        val expectedBytes = readTestAsset("regions/region_data_${countryName ?: "WW"}")
        val sendBytes = notNullableFlipperRequest.map {
            it.data.storageWriteRequest.file.data.toByteArray()
        }.flatten()
        Assert.assertTrue(expectedBytes.contentEquals(sendBytes))
    }
}