package com.flipperdevices.bridge.connection.feature.alarm.impl.api

import com.flipperdevices.bridge.connection.feature.alarm.api.FAlarmFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.core.ktx.jre.toThrowableFlow
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.storage.InfoRequest
import com.flipperdevices.protobuf.system.PlayAudiovisualAlertRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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