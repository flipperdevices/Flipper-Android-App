package com.flipperdevices.bridge.connection.feature.appstart.impl.api

import com.flipperdevices.bridge.connection.feature.appstart.api.FAppStartFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.app.StartRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import okio.Path

class FAppStartFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
) : FAppStartFeatureApi,
    LogTagProvider {
    override val TAG = "FAlarmFeatureApi"

    override suspend fun startApp(path: Path): Result<Unit> {
        return rpcFeatureApi.requestOnce(
            Main(
                app_start_request = StartRequest(path.toString())
            ).wrapToRequest()
        ).map { }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
        ): FAppStartFeatureApiImpl
    }
}
