package com.flipperdevices.updater.tasks

import androidx.test.platform.app.InstrumentationRegistry
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.core.ktx.jre.flatten
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.downloader.api.DownloaderApiImpl
import com.flipperdevices.updater.impl.model.RegionProvisioning
import com.flipperdevices.updater.impl.tasks.RegionProvisioningHelper
import com.flipperdevices.updater.impl.tasks.SubGhzProvisioningHelper
import com.flipperdevices.updater.impl.tasks.SubGhzProvisioningHelperImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(ParameterizedRobolectricTestRunner::class)
// https://github.com/robolectric/robolectric/discussions/7338
@Config(sdk = [30])
class SubGhzProvisioningHelperTest(
    private val countryName: String?
) {
    private lateinit var downloaderApi: DownloaderApi
    private lateinit var regionProvisioningHelper: RegionProvisioningHelper
    private lateinit var metricApi: MetricApi
    private lateinit var underTest: SubGhzProvisioningHelper

    @Before
    fun setUp() = runTest {
        val mockEngine = MockEngine { request ->
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
            context = mock(),
            client = client,
            downloadAndUnpackDelegate = mock()
        )
        regionProvisioningHelper = mock()
        metricApi = mock()
        underTest = SubGhzProvisioningHelperImpl(
            downloaderApi,
            regionProvisioningHelper,
            metricApi
        )
    }

    @Test
    fun `check region protobuf`() = runTest {
        whenever(regionProvisioningHelper.provideRegion(eq("TT"))).doReturn(
            RegionProvisioning(
                regionFromNetwork = countryName,
                regionFromSim = null,
                regionFromIp = null,
                regionSystem = null,
                isRoaming = false
            )
        )

        var flipperRequests: List<FlipperRequest>? = null
        val requestApi: FlipperRequestApi = mock()
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

        underTest.provideAndUploadSubGhz(requestApi)

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

private fun readTestAsset(path: String): ByteArray {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    return context.resources.assets.open(path).use { it.readBytes() }
}
