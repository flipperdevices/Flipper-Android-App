package com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.fullinforpc

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.info.api.model.FlipperRpcInformation
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.mapper.FlipperRpcInfoMapper
import com.flipperdevices.info.impl.viewmodel.deviceinfo.helpers.mapper.InternalFlipperRpcInformationRaw
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal abstract class FlipperFullInfoRpcApi(
    private val mapper: FlipperRpcInfoMapper
) {
    fun getRawDataFlow(
        requestApi: FlipperRequestApi
    ): Flow<FlipperRpcInformation> = flow {
        var rpcRaw = InternalFlipperRpcInformationRaw()
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
        requestApi: FlipperRequestApi,
        onNewPair: suspend (key: String, value: String) -> Unit
    )
}