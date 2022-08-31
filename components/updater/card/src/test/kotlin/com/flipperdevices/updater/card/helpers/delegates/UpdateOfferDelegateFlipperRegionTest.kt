package com.flipperdevices.updater.card.helpers.delegates

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.updater.card.helpers.FileExistHelper
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class UpdateOfferDelegateFlipperRegionTest {
    private val serviceApi: FlipperServiceApi = mockk()
    private val fileExistHelper: FileExistHelper = mockk()
    private val delegate: UpdateOfferDelegate = UpdateOfferDelegateFlipperRegionFile(
        fileExistHelper = fileExistHelper
    )

    @Test
    fun `Manifest not exist`() = runTest {
        every {
            fileExistHelper.isFileExist(Constants.PATH.REGION_FILE, serviceApi.requestApi)
        } returns flowOf(false)
        delegate.isRequire(serviceApi).collect {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun `Manifest exist`() = runTest {
        every {
            fileExistHelper.isFileExist(Constants.PATH.REGION_FILE, serviceApi.requestApi)
        } returns flowOf(true)
        delegate.isRequire(serviceApi).collect {
            Assert.assertFalse(it)
        }
    }
}
