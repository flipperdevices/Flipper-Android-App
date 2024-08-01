package com.flipperdevices.bridge.connection.feature.rpcinfo.impl.fullinforpc

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper.FlipperRpcInfoMapper
import com.flipperdevices.bridge.connection.feature.rpcinfo.impl.mapper.InternalFlipperRpcInformationRaw
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperRpcInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal abstract class FlipperFullInfoRpcApi<KEY>(
    private val mapper: FlipperRpcInfoMapper<KEY>
) {
    fun getRawDataFlow(
        requestApi: FRpcFeatureApi
    ): Flow<FlipperRpcInformation> = flow {
        var rpcRaw = InternalFlipperRpcInformationRaw<KEY>()
        val mutex = Mutex()
        getRawDataFlow(requestApi) { key, value ->
            mutex.withLock {
                rpcRaw = rpcRaw.copy(
                    otherFields = rpcRaw.otherFields.plus(key to value)
                )
                emit(mapper.map(rpcRaw))
            }
        }
    }

    abstract suspend fun getRawDataFlow(
        requestApi: FRpcFeatureApi,
        onNewPair: suspend (key: KEY, value: String) -> Unit
    )
}
