package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItemWithHash
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FileExistHelperTest {

    private val fListingStorageApi: FListingStorageApi = mockk()
    private lateinit var fileExistHelper: FileExistHelper

    @Before
    fun setup() {
        fileExistHelper = FileExistHelper()
    }

    @Test
    fun `exist file`() = runTest {
        every {
            runBlocking {
                fListingStorageApi.lsWithMd5Flow("")
            }
        } returns flow {
            Result.success(
                ListingItemWithHash(
                    fileName = "filename",
                    fileType = null,
                    size = 0L,
                    md5 = "md5"
                )
            )
        }
        val existFileFlow = fileExistHelper.isFileExist("", fListingStorageApi)
        existFileFlow.collectLatest {
            Assert.assertEquals(it, true)
        }
    }

    @Test
    fun `Not exist file`() = runTest {
        every {
            runBlocking {
                fListingStorageApi.lsWithMd5Flow("")
            }
        } returns flow {
            Result.failure<ListingItemWithHash>(Throwable("Test throwable path not exists"))
        }
        val existFileFlow = fileExistHelper.isFileExist("", fListingStorageApi)
        existFileFlow.collectLatest {
            Assert.assertEquals(it, false)
        }
    }
}
