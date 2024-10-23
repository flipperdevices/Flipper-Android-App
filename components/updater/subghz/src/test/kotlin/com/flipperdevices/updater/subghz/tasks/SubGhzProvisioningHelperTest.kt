package com.flipperdevices.updater.subghz.tasks

import android.os.Build
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.ktx.jre.flatten
import com.flipperdevices.core.test.readTestAsset
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.downloader.api.DownloaderApiImpl
import com.flipperdevices.updater.subghz.helpers.RegionProvisioningHelper
import com.flipperdevices.updater.subghz.helpers.SkipProvisioningHelper
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelperImpl
import com.flipperdevices.updater.subghz.model.RegionProvisioning
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class SubGhzProvisioningHelperTest(
    private val countryName: String?
) {
    private lateinit var downloaderApi: DownloaderApi
    private lateinit var regionProvisioningHelper: RegionProvisioningHelper
    private lateinit var metricApi: MetricApi
    private lateinit var skipProvisioningHelper: SkipProvisioningHelper
    private lateinit var underTest: SubGhzProvisioningHelper

    @Before
    fun setUp() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = readTestAsset("regions.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
        downloaderApi = DownloaderApiImpl(
            client = client,
            downloadAndUnpackDelegateApi = mockk(),
            storageProvider = mockk()
        )
        regionProvisioningHelper = mockk()

        coEvery { regionProvisioningHelper.provideRegion(eq("TT")) } returns RegionProvisioning(
            regionFromNetwork = countryName,
            regionFromSim = null,
            regionFromIp = null,
            regionSystem = null,
            isRoaming = false
        )

        metricApi = mockk(relaxUnitFun = true)
        skipProvisioningHelper = mockk {
            coEvery { shouldSkipProvisioning(any()) } returns false
        }
        underTest = SubGhzProvisioningHelperImpl(
            downloaderApi,
            regionProvisioningHelper,
            metricApi,
            skipProvisioningHelper
        )
    }

    @Test
    fun `check region protobuf`() = runTest {
        var flipperRequests: List<FlipperRequest>? = null
        val requestApi: FlipperRequestApi = mockk()
        val serviceApi: FlipperServiceApi = mockk()
        every { serviceApi.requestApi } returns requestApi
        coEvery { requestApi.request(any(), any()) } coAnswers {
            flipperRequests = runBlocking {
                arg<Flow<FlipperRequest>>(0)
                    .toList()
            }
            return@coAnswers main {
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

    @Test
    fun `Get region`() = runTest {
        Assert.assertEquals(underTest.getRegion(), countryName)
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters
        fun data() = listOf(
            "AL", "AM", "AT", "AU", "AZ", "BA", "BE", "BG", "BY", "CA", "CH", "CY",
            "CZ", "DE", "DK", "DZ", "EE", "EG", "ES", "FI", "FR", "GB", "GE", "GR",
            "HR", "HU", "IE", "IL", "IS", "IT", "JO", "JP", "LB", "LI", "LT", "LU",
            "LV", "LY", "MA", "MD", "ME", "MK", "MT", "NL", "NO", "PL", "PS", "PT",
            "RO", "RS", "RU", "SE", "SI", "SK", "SY", "TN", "TR", "UA", "US", "XK",
            "WW", null
        )
    }
}
