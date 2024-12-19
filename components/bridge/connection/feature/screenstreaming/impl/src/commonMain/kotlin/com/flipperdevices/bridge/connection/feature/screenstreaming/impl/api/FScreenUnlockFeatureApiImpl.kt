package com.flipperdevices.bridge.connection.feature.screenstreaming.impl.api

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.screenstreaming.api.FScreenUnlockFeatureApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.desktop.UnlockRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FScreenUnlockFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
) : FScreenUnlockFeatureApi,
    LogTagProvider {
    override val TAG = "FScreenUnlockFeatureApi"

    override suspend fun unlock(): Result<Main> {
        return rpcFeatureApi.requestOnce(
            Main(
                desktop_unlock_request = UnlockRequest()
            ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
        )
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
        ): FScreenUnlockFeatureApiImpl
    }
}
