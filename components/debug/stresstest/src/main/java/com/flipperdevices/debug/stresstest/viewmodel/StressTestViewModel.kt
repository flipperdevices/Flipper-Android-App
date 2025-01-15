package com.flipperdevices.debug.stresstest.viewmodel

import androidx.compose.ui.graphics.Color
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.bridge.connection.feature.storage.api.fm.FFileDownloadApi
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.bridge.connection.transport.common.api.serial.FlipperSerialSpeed
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.progress.copyWithProgress
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.debug.stresstest.model.LogLine
import com.flipperdevices.debug.stresstest.model.StressTestState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import kotlin.random.Random
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

const val BUFFER_SIZE = 20 * 1024
const val TEST_FILE = "/ext/stresstest_mobile.tmp"

@Suppress("TooManyFunctions")
class StressTestViewModel @Inject constructor(
    private val fFeatureProvider: FFeatureProvider,
    private val fDeviceOrchestrator: FDeviceOrchestrator
) : DecomposeViewModel() {
    private val isStressTestRunning = AtomicBoolean(false)
    private val debugLog = MutableStateFlow(persistentListOf<LogLine>())
    private val stressTestState = MutableStateFlow(StressTestState())
    private val speedState = MutableStateFlow(FlipperSerialSpeed())
    private var byteBuffer = ByteArray(size = BUFFER_SIZE)
    private var stressTestJob: Job? = null

    init {
        subscribeToConnectionStateUpdate()
        subscribeToSpeedStateUpdate()
    }

    fun startBruteforce() {
        stressTestJob = viewModelScope.launch {
            if (!isStressTestRunning.compareAndSet(false, true)) {
                return@launch
            }
            writeToLog("Stress test starting")

            while (isActive && isStressTestRunning.get()) {
                removeTemporaryFile()
                fillBuffer()
                sendBufferToFile()
                receiveBufferFromFileAndCheck()
            }

            writeToLog("Stress test stopping")
        }
    }

    fun getDebugLog(): StateFlow<ImmutableList<LogLine>> = debugLog

    fun getStressTestState(): StateFlow<StressTestState> = stressTestState

    fun getSpeed(): StateFlow<FlipperSerialSpeed> = speedState

    fun stopBruteforce() {
        viewModelScope.launch {
            stressTestJob?.cancelAndJoin()
            isStressTestRunning.set(false)
        }
    }

    private fun writeToLog(log: String, color: Color? = null) {
        debugLog.update {
            return@update it.plus(LogLine(log, color)).toPersistentList()
        }
    }

    private suspend fun removeTemporaryFile() {
        withContext(FlipperDispatchers.workStealingDispatcher) {
            val fStorageFeatureApi = fFeatureProvider
                .getSync<FStorageFeatureApi>()
                ?: return@withContext
            fStorageFeatureApi.deleteApi()
                .delete(TEST_FILE)
                .onFailure { writeToLog("Could not delete $TEST_FILE") }
                .onSuccess { writeToLog("Remove $TEST_FILE") }
        }
    }

    private suspend fun fillBuffer() = withContext(FlipperDispatchers.workStealingDispatcher) {
        byteBuffer = Random.nextBytes(byteBuffer)
    }

    private suspend fun sendBufferToFile(
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        val fStorageFeatureApi = fFeatureProvider
            .getSync<FStorageFeatureApi>()
            ?: return@withContext

        byteBuffer.inputStream().source().buffer().use { source ->
            fStorageFeatureApi.uploadApi().sink(TEST_FILE).use { sink ->
                source.copyWithProgress(
                    sink,
                    { _, _ -> },
                    sourceLength = { byteBuffer.size.toLong() }
                )
            }
        }

        writeToLog("Write file with length $BUFFER_SIZE successfully")
    }


    suspend fun FFileDownloadApi.readByteArray(pathOnFlipper: String) = coroutineScope {
        source(pathOnFlipper, this)
            .buffer()
            .readByteArray()
    }

    private suspend fun receiveBufferFromFileAndCheck(
    ) = withContext(FlipperDispatchers.workStealingDispatcher) {
        val fStorageFeatureApi = fFeatureProvider
            .getSync<FStorageFeatureApi>()
            ?: return@withContext

        val isEquals = fStorageFeatureApi.downloadApi()
            .readByteArray(TEST_FILE)
            .also { writeToLog("Read file with length ${it.size} successfully") }
            .contentEquals(byteBuffer)

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

    private fun FDeviceConnectStatus.toHumanReadableName(): String {
        return when (this) {
            is FDeviceConnectStatus.Connected -> "Connected-${this.device.humanReadableName}"
            is FDeviceConnectStatus.Connecting -> "Connecting-${this.device.humanReadableName}"
            is FDeviceConnectStatus.Disconnected -> "Disconnected"
            is FDeviceConnectStatus.Disconnecting -> "Disconnecting"
        }
    }

    private fun subscribeToConnectionStateUpdate() {
        fDeviceOrchestrator
            .getState()
            .onEach { writeToLog("Connection state is ${it.toHumanReadableName()}") }
            .launchIn(viewModelScope)
    }

    private fun subscribeToSpeedStateUpdate() = viewModelScope.launch {
        fFeatureProvider.get<FSpeedFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FSpeedFeatureApi> }
            .map { status -> status?.featureApi }
            .flatMapLatest { feature -> feature?.getSpeed() ?: flowOf(null) }
            .onEach { speed -> speedState.emit(speed ?: FlipperSerialSpeed()) }
            .launchIn(viewModelScope)
    }
}
