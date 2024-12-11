package com.flipperdevices.bridge.connection.feature.alarm.impl.api

import com.flipperdevices.bridge.connection.feature.alarm.api.FAlarmFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.system.PlayAudiovisualAlertRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FAlarmFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
) : FAlarmFeatureApi,
    LogTagProvider {
    override val TAG = "FAlarmFeatureApi"

    override suspend fun makeSound() {
        rpcFeatureApi.requestWithoutAnswer(
            Main(
                system_play_audiovisual_alert_request = PlayAudiovisualAlertRequest()
            ).wrapToRequest()
        )
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
        ): FAlarmFeatureApiImpl
    }
}
