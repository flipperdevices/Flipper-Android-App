package com.flipperdevices.keyscreen.emulate.viewmodel.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.test.TimberRule
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.protobuf.app.appStateResponse
import com.flipperdevices.protobuf.main
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test

private val KEY_TYPE = FlipperKeyType.SUB_GHZ
private val TEST_KEY_PATH = FlipperFilePath(KEY_TYPE.flipperDir, "test.${KEY_TYPE.extension}")

private val responseOk = main {
    commandStatus = Flipper.CommandStatus.OK
}
private val appStateResponseOk = main {
    commandStatus = Flipper.CommandStatus.OK
    appStateResponse = appStateResponse {
        state = Application.AppState.APP_STARTED
    }
}

class EmulateHelperTest {
    private lateinit var flipperKey: FlipperKey
    private lateinit var requestApi: FlipperRequestApi
    private lateinit var underTest: EmulateHelper

    @Before
    fun setUp() {
        underTest = EmulateHelper()
        requestApi = mockk() {
            every { notificationFlow() } coAnswers {
                flowOf(appStateResponseOk)
            }
        }
        mockFlipperRequest()
        flipperKey = FlipperKey(
            mainFile = FlipperFile(TEST_KEY_PATH, FlipperKeyContent.RawData(byteArrayOf())),
            deleted = false,
            synchronized = false
        )
    }

    @Test
    fun `respect emulate order`() = runTest {
        val callOrder = mutableListOf<String>()
        mockFlipperRequest(
            onAppLoadFileResponse = {
                callOrder.add("AppLoad")
                responseOk
            },
            onAppButtonReleaseResponse = {
                callOrder.add("ButtonRelease")
                responseOk
            }
        )
        val startJob = launch(Dispatchers.Default) {
            underTest.startEmulate(
                this,
                requestApi,
                KEY_TYPE,
                flipperKey
            )
        }
        val stopJob = launch(UnconfinedTestDispatcher()) {
            underTest.stopEmulate(this, requestApi)
        }

        stopJob.join()
        startJob.join()

        Assert.assertEquals("[AppLoad, ButtonRelease]", callOrder.toString())
    }

    private fun mockFlipperRequest(
        onAppStartResponse: () -> Flipper.Main = { responseOk },
        onAppLoadFileResponse: () -> Flipper.Main = { responseOk },
        onAppButtonPressResponse: () -> Flipper.Main = { responseOk },
        onAppButtonReleaseResponse: () -> Flipper.Main = { responseOk }
    ) {
        coEvery { requestApi.request(any(), any()) } coAnswers {
            val flipperRequestFlow = it.invocation.args.first() as Flow<*>
            val request = flipperRequestFlow.first() as FlipperRequest
            val data = request.data
            when {
                data.hasAppStartRequest() -> onAppStartResponse()
                data.hasAppLoadFileRequest() -> onAppLoadFileResponse()
                data.hasAppButtonPressRequest() -> onAppButtonPressResponse()
                data.hasAppButtonReleaseRequest() -> onAppButtonReleaseResponse()
                else -> responseOk
            }
        }
    }

    companion object {
        @get:ClassRule
        @JvmStatic
        var timberRule = TimberRule()
    }
}
