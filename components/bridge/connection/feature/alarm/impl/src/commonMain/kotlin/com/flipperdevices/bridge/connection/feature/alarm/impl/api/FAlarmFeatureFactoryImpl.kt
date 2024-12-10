package com.flipperdevices.bridge.connection.feature.alarm.impl.api

import com.flipperdevices.bridge.connection.feature.alarm.api.FAlarmFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeature
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureQualifier
import com.flipperdevices.bridge.connection.feature.common.api.FUnsafeDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.FConnectedDeviceApi
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@FDeviceFeatureQualifier(FDeviceFeature.ALARM)
@ContributesMultibinding(AppGraph::class, FDeviceFeatureApi.Factory::class)
class FAlarmFeatureFactoryImpl @Inject constructor(
    private val factory: FAlarmFeatureApiImpl.InternalFactory
) : FDeviceFeatureApi.Factory {
    override suspend fun invoke(
        unsafeFeatureDeviceApi: FUnsafeDeviceFeatureApi,
        scope: CoroutineScope,
        connectedDevice: FConnectedDeviceApi
    ): FDeviceFeatureApi? {
        val rpcApi = unsafeFeatureDeviceApi.getUnsafe(FRpcFeatureApi::class) ?: return null
        return factory(
            rpcFeatureApi = rpcApi,
        )
    }
}
