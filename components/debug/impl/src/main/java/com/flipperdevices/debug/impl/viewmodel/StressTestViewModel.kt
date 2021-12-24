package com.flipperdevices.debug.impl.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.manager.delegates.toHumanReadableString
import com.flipperdevices.bridge.api.model.FlipperSerialSpeed
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.protobuf.ProtobufConstants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.split
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.debug.impl.di.DebugComponent
import com.flipperdevices.debug.impl.model.LogLine
import com.flipperdevices.debug.impl.model.StressTestState
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.readRequest
import com.flipperdevices.protobuf.storage.writeRequest
import com.google.protobuf.ByteString
import java.nio.ByteBuffer
import java.util.Arrays
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val BUFFER_SIZE = 20 * 1024
const val TEST_FILE = "/any/stresstest_mobile.tmp"

@Suppress("TooManyFunctions")
class StressTestViewModel : LifecycleViewModel() {
    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    private val isStressTestRunning = AtomicBoolean(false)
    private val debugLog = MutableStateFlow(emptyList<LogLine>())
    private val stressTestState = MutableStateFlow(StressTestState())
    private val speedState = MutableStateFlow(FlipperSerialSpeed())
    private var byteBuffer = ByteArray(size = BUFFER_SIZE)

    init {
        ComponentHolder.component<DebugComponent>().inject(this)
        serviceProvider.provideServiceApi(this) {
            subscribeToConnectionStateUpdate(it)
            subscribeToSpeedStateUpdate(it)
        }
    }

    fun startBruteforce() {
        serviceProvider.provideServiceApi(this) { serviceApi ->
            viewModelScope.launch {
                if (!isStressTestRunning.compareAndSet(false, true)) {
                    return@launch
                }
                writeToLog("Stress test starting")

                while (isStressTestRunning.get()) {
                    removeTemporaryFile(serviceApi.requestApi)
                    fillBuffer()
                    sendBufferToFile(serviceApi.requestApi)
                    receiveBufferFromFileAndCheck(serviceApi.requestApi)
                }

                writeToLog("Stress test stopping")
            }
        }
    }

    fun getDebugLog(): StateFlow<List<LogLine>> = debugLog

    fun getStressTestState(): StateFlow<StressTestState> = stressTestState

    fun getSpeed(): StateFlow<FlipperSerialSpeed> = speedState

    fun stopBruteforce() {
        isStressTestRunning.set(false)
    }

    private fun writeToLog(log: String, color: Color = Color.Black) {
        debugLog.update {
            return@update it.plus(LogLine(log, color))
        }
    }

    private suspend fun removeTemporaryFile(
        requestApi: FlipperRequestApi
    ) = withContext(Dispatchers.IO) {
        requestApi.request(
            main {
                storageDeleteRequest = deleteRequest {
                    path = TEST_FILE
                }
            }.wrapToRequest()
        ).single()
        writeToLog("Remove $TEST_FILE")
    }

    private suspend fun fillBuffer() = withContext(Dispatchers.IO) {
        byteBuffer = Random.nextBytes(byteBuffer)
    }

    private suspend fun sendBufferToFile(
        requestApi: FlipperRequestApi
    ) = withContext(Dispatchers.IO) {
        val splittedBytes = byteBuffer
            .split(ProtobufConstants.MAX_FILE_DATA)
        val requests = splittedBytes
            .mapIndexed { index, bytes ->
                getWriteRequest(bytes, isLast = index == splittedBytes.lastIndex)
            }
            .asFlow()
        requestApi.request(requests)
        writeToLog("Write file with length $BUFFER_SIZE successfully")
    }

    private fun getWriteRequest(bytes: ByteArray, isLast: Boolean) = main {
        hasNext = !isLast
        storageWriteRequest = writeRequest {
            path = TEST_FILE
            file = file { data = ByteString.copyFrom(bytes) }
        }
    }.wrapToRequest()

    private suspend fun receiveBufferFromFileAndCheck(
        requestApi: FlipperRequestApi
    ) = withContext(Dispatchers.IO) {
        val receiveBytes = ByteBuffer.allocate(byteBuffer.size)
        var bytesCount = 0L
        requestApi.request(
            main {
                storageReadRequest = readRequest {
                    path = TEST_FILE
                }
            }.wrapToRequest()
        ).collect {
            val receivedBytesArray = it.storageReadResponse.file.data.toByteArray()
            bytesCount += receivedBytesArray.size
            receiveBytes.put(receivedBytesArray)
        }
        writeToLog("Read file with length $bytesCount successfully")

        val isEquals = Arrays.equals(receiveBytes.array(), byteBuffer)
        if (isEquals) {
            writeToLog("Buffer equals!", Color.Green)
            stressTestState.update {
                it.copy(successfulCount = it.successfulCount + 1)
            }
        } else {
            writeToLog("!!! Buffer NOT equals !!!", Color.Red)
            stressTestState.update {
                it.copy(errorCount = it.errorCount + 1)
            }
        }
    }

    private fun subscribeToConnectionStateUpdate(serviceApi: FlipperServiceApi) {
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            writeToLog("Connection state is ${it.toHumanReadableString()}")
        }.launchIn(viewModelScope)
    }

    private fun subscribeToSpeedStateUpdate(
        serviceApi: FlipperServiceApi
    ) = viewModelScope.launch {
        serviceApi.requestApi.getSpeed().onEach {
            speedState.emit(it)
        }.launchIn(viewModelScope)
    }
}
