package com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.info.api.model.FlipperInformationStatus
import com.flipperdevices.info.api.model.FlipperRpcInformation
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.fullinforpc.DeprecatedFlipperFullInfoRpcApi
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.fullinforpc.NewFlipperFullInfoRpcApi
import com.flipperdevices.shake2report.api.Shake2ReportApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

interface FlipperRpcInformationApi {
    fun getRpcInformationFlow(): StateFlow<FlipperInformationStatus<FlipperRpcInformation>>
    suspend fun invalidate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        force: Boolean = false
    )

    suspend fun reset()
}

@ContributesBinding(AppGraph::class, FlipperRpcInformationApi::class)
class FlipperRpcInformationApiImpl @Inject constructor(
    private val shake2ReportApi: Shake2ReportApi
) : FlipperRpcInformationApi, LogTagProvider {
    override val TAG = "FlipperRpcInformationApi"

    private val mutex = Mutex()
    private var alreadyRequested = false
    private var job: Job? = null
    private val rpcInformationFlow =
        MutableStateFlow<FlipperInformationStatus<FlipperRpcInformation>>(
            FlipperInformationStatus.NotStarted()
        )

    override fun getRpcInformationFlow() = rpcInformationFlow.asStateFlow()
    override suspend fun invalidate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        force: Boolean
    ) = withLock(mutex, "invalidate") {
        if (force.not() && alreadyRequested) {
            return@withLock
        }
        alreadyRequested = true

        job?.cancelAndJoin()
        job = scope.launch {
            invalidateInternal(serviceApi)
        }
    }

    override suspend fun reset() = withLock(mutex, "reset") {
        alreadyRequested = false
        job?.cancelAndJoin()
        rpcInformationFlow.emit(FlipperInformationStatus.NotStarted())
    }

    private suspend fun invalidateInternal(
        serviceApi: FlipperServiceApi
    ) {
        rpcInformationFlow.emit(FlipperInformationStatus.InProgress(FlipperRpcInformation()))
        val version = serviceApi.flipperVersionApi.getVersionInformationFlow()
            .filterNotNull()
            .first()
        val flipperFullInfoRpcApi = if (version >= Constants.API_SUPPORTED_GET_REQUEST) {
            NewFlipperFullInfoRpcApi()
        } else {
            DeprecatedFlipperFullInfoRpcApi()
        }

        flipperFullInfoRpcApi
            .getRawDataFlow(serviceApi.requestApi)
            .collect { rpcInformation ->
                rpcInformationFlow.update {
                    if (it is FlipperInformationStatus.InProgress) {
                        FlipperInformationStatus.InProgress(rpcInformation)
                    } else {
                        it
                    }
                }
                shake2ReportApi.updateRpcInformation(rpcInformation)
            }

        rpcInformationFlow.update {
            if (it is FlipperInformationStatus.InProgress) {
                FlipperInformationStatus.Ready(it.data)
            } else {
                it
            }
        }
    }
}
