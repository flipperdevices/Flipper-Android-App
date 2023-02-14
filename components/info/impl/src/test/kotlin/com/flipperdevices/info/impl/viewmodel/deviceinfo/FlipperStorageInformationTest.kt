package com.flipperdevices.info.impl.viewmodel.deviceinfo

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.info.impl.model.deviceinfo.FlipperStorageInformation
import com.flipperdevices.info.impl.model.deviceinfo.StorageStats
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.FlipperInformationStatus
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.FlipperStorageInformationApi
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.FlipperStorageInformationApiImpl
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.infoRequest
import com.flipperdevices.protobuf.storage.infoResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FlipperStorageInformationTest {
    private lateinit var requestApi: FlipperRequestApi

    private lateinit var metricApi: MetricApi
    private lateinit var underTest: FlipperStorageInformationApi

    @Before
    fun setUp() {
        requestApi = mockk()

        metricApi = mockk(relaxUnitFun = true)
        underTest = FlipperStorageInformationApiImpl(metricApi)
    }

    @Test
    fun `request storage info first time`() = runTest {
        val scope = this

        mockStorageInfo(requestApi, "/int/", 100, 200)
        mockStorageInfo(requestApi, "/ext/", 400, 800)

        underTest.invalidate(scope, requestApi)

        scope.testScheduler.advanceUntilIdle()

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