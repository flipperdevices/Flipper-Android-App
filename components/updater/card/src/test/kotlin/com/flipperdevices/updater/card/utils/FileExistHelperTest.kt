package com.flipperdevices.updater.card.utils

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.md5sumResponse
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FileExistHelperTest {

    private lateinit var requestApi: FlipperRequestApi
    private lateinit var fileExistHelper: FileExistHelper

    @Before
    fun setup() {
        fileExistHelper = FileExistHelper()
        requestApi = mock()
    }

    @Test
    fun `exist file`() = runTest {
        whenever(requestApi.request(command = any())).doReturn(
            flow {
                main {
                    storageMd5SumResponse = md5sumResponse {
                        md5Sum = "md5Sum"
                    }
                }
            }
        )
        val existFileFlow = fileExistHelper.isFileExist("", requestApi)
        existFileFlow.collectLatest {
            Assert.assertEquals(it, true)
        }
    }

    @Test
    fun `Not exist file`() = runTest {
        whenever(requestApi.request(command = any())).doReturn(
            flow {
                main {
                    // empty response
                }
            }
        )
        val existFileFlow = fileExistHelper.isFileExist("", requestApi)
        existFileFlow.collectLatest {
            Assert.assertEquals(it, false)
        }
    }
}
