package com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.forEachIterable
import com.flipperdevices.core.ktx.jre.map
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.info.api.model.FlipperRpcInformation
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.system.deviceInfoRequest
import com.flipperdevices.protobuf.system.powerInfoRequest
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FlipperRpcInformationApi {
    fun getRequestRpcInformationStatus(): StateFlow<FlipperRequestRpcInformationStatus>
    fun getRpcInformationFlow(): StateFlow<FlipperRpcInformation>
    suspend fun invalidate(requestApi: FlipperRequestApi)
}

private const val FLIPPER_KEY_HARDWARE_COLOR = "hardware_color"

@ContributesBinding(AppGraph::class, FlipperRpcInformationApi::class)
class FlipperRpcInformationApiImpl @Inject constructor(
    private val metricApi: MetricApi,
    private val shake2ReportApi: Shake2ReportApi,
    private val dataStore: DataStore<PairSettings>
) : FlipperRpcInformationApi, LogTagProvider {
    override val TAG = "FlipperRpcInformationApi"

    private val scope = CoroutineScope(Dispatchers.Default) // TODO remove it
    private val mutex = Mutex()
    private val rpcInformationFlow = MutableStateFlow(
        InternalFlipperRpcInformationRaw()
    )
    private val requestStatusFlow = MutableStateFlow<FlipperRequestRpcInformationStatus>(
        FlipperRequestRpcInformationStatus.NotStarted
    )
    private val requestJobs = mutableListOf<Job>()

    init {
        rpcInformationFlow.onEach {
            shake2ReportApi.updateRpcInformation(DeviceInfoHelper.mapRawRpcInformation(it))
        }.launchIn(scope + Dispatchers.Default)
    }

    override fun getRequestRpcInformationStatus() = requestStatusFlow
    override fun getRpcInformationFlow() = rpcInformationFlow
        .map(scope + Dispatchers.Default) {
            DeviceInfoHelper.mapRawRpcInformation(it)
        }

    suspend fun initialize(requestApi: FlipperRequestApi) = withLock(mutex, "initialize") {
        requestStatusFlow.emit(FlipperRequestRpcInformationStatus.InProgress())
        requestJobs += scope.launch(Dispatchers.Default) {
            requestApi.request(
                main {
                    systemDeviceInfoRequest = deviceInfoRequest { }
                }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
            ).collect { response ->
                if (!response.hasSystemDeviceInfoResponse()) {
                    return@collect
                }
                onApplyInfo(
                    response.systemDeviceInfoResponse.key,
                    response.systemDeviceInfoResponse.value
                )
            }
            requestApi.request(
                main {
                    systemPowerInfoRequest = powerInfoRequest { }
                }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
            ).collect { response ->
                if (!response.hasSystemPowerInfoResponse()) {
                    return@collect
                }
                onApplyInfo(
                    response.systemPowerInfoResponse.key,
                    response.systemPowerInfoResponse.value
                )
            }
            requestStatusFlow.update {
                if (it is FlipperRequestRpcInformationStatus.InProgress) {
                    it.copy(rpcDeviceInfoRequestFinished = true)
                } else {
                    it
                }
            }
        }
    }

    override suspend fun invalidate(requestApi: FlipperRequestApi) {
        reset()
        initialize(requestApi)
    }

    suspend fun reset() = withLock(mutex, "reset") {
        requestJobs.forEachIterable {
            it.cancelAndJoin()
        }
        requestStatusFlow.emit(FlipperRequestRpcInformationStatus.NotStarted)
        rpcInformationFlow.emit(InternalFlipperRpcInformationRaw())
    }

    private suspend fun onApplyInfo(
        key: String,
        value: String
    ) = withContext(Dispatchers.Default) {
        verbose { "Receive: $key=$value" }

        if (key == FLIPPER_KEY_HARDWARE_COLOR) {
            val id = value.toIntOrNull()
            if (id != null) {
                dataStore.updateData {
                    it.toBuilder()
                        .setHardwareColor(
                            when (id) {
                                HardwareColor.WHITE_VALUE -> HardwareColor.WHITE
                                HardwareColor.BLACK_VALUE -> HardwareColor.BLACK
                                else -> HardwareColor.WHITE
                            }
                        )
                        .build()
                }
            }
        }

        rpcInformationFlow.update { rpcInformation ->
            rpcInformation.copy(
                otherFields = rpcInformation.otherFields.plus(key to value)
            )
        }
    }
}
