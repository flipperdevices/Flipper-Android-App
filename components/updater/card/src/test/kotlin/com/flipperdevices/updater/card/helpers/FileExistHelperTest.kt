package com.flipperdevices.updater.card.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.md5sumResponse
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FileExistHelperTest {

    private lateinit var requestApi: FlipperRequestApi
    private lateinit var fileExistHelper: FileExistHelper

    @Before
    fun setup() {
        fileExistHelper = FileExistHelper()
        requestApi = mockk()
    }

    @Test
    fun `exist file`() = runTest {
        every { requestApi.request(command = any()) } returns flow {
            main {
                storageMd5SumResponse = md5sumResponse {
                    md5Sum = "md5Sum"
                }
            }
        }
        val existFileFlow = fileExistHelper.isFileExist("", requestApi)
        existFileFlow.collectLatest {
            Assert.assertEquals(it, true)
        }
    }

    @Test
    fun `Not exist file`() = runTest {
        every { requestApi.request(command = any()) } returns flow {
            main {
            }
        }
        val existFileFlow = fileExistHelper.isFileExist("", requestApi)
        existFileFlow.collectLatest {
            Assert.assertEquals(it, false)
        }
    }
}
