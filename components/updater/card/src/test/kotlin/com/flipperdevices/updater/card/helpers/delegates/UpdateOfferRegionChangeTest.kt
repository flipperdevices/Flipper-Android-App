package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.protobuf.Region
import com.flipperdevices.updater.subghz.helpers.REGION_FILE
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.test.runTest
import okio.ByteString.Companion.encode
import okio.source
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class UpdateOfferRegionChangeTest {

    private val fFileDownloadApi: FFileDownloadApi = mockk()
    private val fStorageFeatureApi: FStorageFeatureApi = mockk()

    private fun region(code: String) = Region(
        country_code = code.encode(Charset.forName("ASCII")),
        bands = emptyList()
    ).encodeByteString()

    private val subGhzProvisioningHelper: SubGhzProvisioningHelper = mockk()
    private val delegate: UpdateOfferDelegate = UpdateOfferRegionChange(
        subGhzProvisioningHelper = subGhzProvisioningHelper
    )

    @Before
    fun setup() {
        mockkObject(TimeHelper)
        every { TimeHelper.getNanoTime() } returns 0L
        coEvery { fStorageFeatureApi.downloadApi() } returns fFileDownloadApi
    }

    @Test
    fun `Same region in storage and now`() = runTest {
        coEvery {
            fFileDownloadApi.source(REGION_FILE, any())
        } returns ByteArrayInputStream(region("UA").toByteArray()).source()
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
        delegate.isRequire(fStorageFeatureApi).collect { Assert.assertFalse(it) }
    }

    @Test
    fun `Different region in storage and now`() = runTest {
        coEvery {
            fFileDownloadApi.source(REGION_FILE, any())
        } returns ByteArrayInputStream(region("USA").toByteArray()).source()
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"
        delegate.isRequire(fStorageFeatureApi).collect { Assert.assertTrue(it) }
    }

    @Test
    fun `Exception when we get current region`() = runTest {
        coEvery {
            fFileDownloadApi.source(REGION_FILE, any())
        } returns ByteArrayInputStream(region("USA").toByteArray()).source()
        coEvery { subGhzProvisioningHelper.getRegion() }.throws(Exception("Some error"))
        delegate.isRequire(fStorageFeatureApi).collect { Assert.assertTrue(it) }
    }

    @Test
    fun `Exception when we get region from flipper`() = runTest {
        coEvery { subGhzProvisioningHelper.getRegion() } returns "UA"

        coEvery { fFileDownloadApi.source(REGION_FILE, any()) } throws Throwable("Test error")
        delegate.isRequire(fStorageFeatureApi).collect { Assert.assertTrue(it) }
    }
}
