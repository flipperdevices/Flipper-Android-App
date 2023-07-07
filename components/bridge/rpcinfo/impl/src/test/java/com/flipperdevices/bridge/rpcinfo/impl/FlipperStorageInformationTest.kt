package com.flipperdevices.bridge.rpcinfo.impl

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpcinfo.api.FlipperStorageInformationApi
import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.rpcinfo.model.StorageStats
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.ktx.jre.TimeHelper
import com.flipperdevices.core.test.PendingCoroutineExceptionHandler
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.infoRequest
import com.flipperdevices.protobuf.storage.infoResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FlipperStorageInformationTest {
    private lateinit var exceptionHandler: PendingCoroutineExceptionHandler

    private lateinit var serviceApi: FlipperServiceApi
    private lateinit var requestApi: FlipperRequestApi

    private lateinit var metricApi: MetricApi
    private lateinit var underTest: FlipperStorageInformationApi

    @Before
    fun setUp() {
        exceptionHandler = PendingCoroutineExceptionHandler()
        mockkObject(TimeHelper)
        every { TimeHelper.getNanoTime() } returns 0L

        requestApi = mockk()
        serviceApi = mockk {
            every { requestApi } returns this@FlipperStorageInformationTest.requestApi
            every { connectionInformationApi } returns mockk {
                every { getConnectionStateFlow() } returns flowOf(
                    ConnectionState.Ready(
                        FlipperSupportedState.READY
                    )
                )
            }
        }

        metricApi = mockk(relaxUnitFun = true)
        underTest = FlipperStorageInformationApiImpl()
    }

    @Test
    fun `not invalidate second time`() = runTest {
        mockStorageInfo(requestApi, "/int/", 100, 200)
        mockStorageInfo(requestApi, "/ext/", 400, 800)

        launch(exceptionHandler) {
            underTest.invalidate(this, serviceApi)
        }

        advanceUntilIdle()

        mockStorageInfo(requestApi, "/int/", 800, 1600)
        mockStorageInfo(requestApi, "/ext/", 1600, 3200)

        launch(exceptionHandler) {
            underTest.invalidate(this, serviceApi)
        }

        advanceUntilIdle()

        val state = underTest.getStorageInformationFlow().first()
        Assert.assertEquals(
            FlipperStorageInformation(
                internalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        free = 100,
                        total = 200
                    )
                ),
                externalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        free = 400,
                        total = 800
                    )
                )
            ),
            state,
        )
    }

    @Test
    fun `force invalidate second time`() = runTest {
        mockStorageInfo(requestApi, "/int/", 100, 200)
        mockStorageInfo(requestApi, "/ext/", 400, 800)

        launch(exceptionHandler) {
            underTest.invalidate(this, serviceApi)
        }

        advanceUntilIdle()

        mockStorageInfo(requestApi, "/int/", 800, 1600)
        mockStorageInfo(requestApi, "/ext/", 1600, 3200)

        launch(exceptionHandler) {
            underTest.invalidate(this, serviceApi, force = true)
        }

        advanceUntilIdle()

        val state = underTest.getStorageInformationFlow().first()
        Assert.assertEquals(
            FlipperStorageInformation(
                internalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        free = 800,
                        total = 1600
                    )
                ),
                externalStorageStatus = FlipperInformationStatus.Ready(
                    StorageStats.Loaded(
                        free = 1600,
                        total = 3200
                    )
                )
            ),
            state,
        )
    }

    @After
    fun cleanUp() {
        exceptionHandler.throwAll()
    }

    private fun mockStorageInfo(
        requestApi: FlipperRequestApi,
        directoryPath: String,
        free: Long,
        total: Long,
    ) {
        coEvery {
            requestApi.request(
                eq(
                    main {
                        storageInfoRequest = infoRequest {
                            path = directoryPath
                        }
                    }.wrapToRequest(FlipperRequestPriority.DEFAULT)
                )
            )
        } returns flowOf(
            main {
                commandStatus = Flipper.CommandStatus.OK
                storageInfoResponse = infoResponse {
                    freeSpace = free
                    totalSpace = total
                }
            }
        )
    }
}
