package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.api

import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.api.FRpcInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.impl.fullinforpc.DeprecatedFlipperFullInfoRpcApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.impl.fullinforpc.NewFlipperFullInfoRpcApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.impl.shaketoreport.FlipperInformationMapping
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.shake2report.api.Shake2ReportApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class FRpcInfoFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
    @Assisted private val getInfoFeatureApi: FGetInfoFeatureApi?,
    private val shake2ReportApi: Shake2ReportApi
) : FRpcInfoFeatureApi, LogTagProvider {
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
        force: Boolean
    ) = withLock(mutex, "invalidate") {
        if (force.not() && alreadyRequested) {
            return@withLock
        }
        alreadyRequested = true

        job?.cancelAndJoin()
        job = scope.launch {
            invalidateInternal()
        }
    }

    override suspend fun reset() = withLock(mutex, "reset") {
        alreadyRequested = false
        job?.cancelAndJoin()
        rpcInformationFlow.emit(FlipperInformationStatus.NotStarted())
    }

    private suspend fun invalidateInternal() {
        rpcInformationFlow.emit(FlipperInformationStatus.InProgress(FlipperRpcInformation()))
        val flipperFullInfoRpcApi = if (
            getInfoFeatureApi != null
        ) {
            NewFlipperFullInfoRpcApi(getInfoFeatureApi)
        } else {
            DeprecatedFlipperFullInfoRpcApi()
        }

        flipperFullInfoRpcApi
            .getRawDataFlow(rpcFeatureApi)
            .collect { rpcInformation ->
                rpcInformationFlow.update {
                    if (it is FlipperInformationStatus.InProgress) {
                        FlipperInformationStatus.InProgress(rpcInformation)
                    } else {
                        it
                    }
                }
                shake2ReportApi.setExtra(FlipperInformationMapping.convert(rpcInformation))
            }

        rpcInformationFlow.update {
            if (it is FlipperInformationStatus.InProgress) {
                FlipperInformationStatus.Ready(it.data)
            } else {
                it
            }
        }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
            getInfoFeatureApi: FGetInfoFeatureApi?,
        ): FRpcInfoFeatureApiImpl
    }
}
