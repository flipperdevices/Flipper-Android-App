package com.flipperdevices.bridge.connection.feature.devicecolor.impl.api

import com.flipperdevices.bridge.connection.feature.devicecolor.api.FDeviceColorFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.property.GetRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FDeviceColorFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
) : FDeviceColorFeatureApi,
    LogTagProvider {
    override val TAG = "FDeviceColorFeatureApi"

    override fun getColor(default: HardwareColor): Flow<HardwareColor> {
        return rpcFeatureApi.request(
            Main(
                property_get_request = GetRequest(
                    key = RPC_KEY_HARDWARE_COLOR
                )
            ).wrapToRequest()
        ).map { result ->
            val intValue = result.getOrNull()
                ?.property_get_response
                ?.value_
                ?.toIntOrNull()
            when (intValue) {
                HardwareColor.WHITE.value -> HardwareColor.WHITE
                HardwareColor.BLACK.value -> HardwareColor.BLACK
                else -> default
            }
        }
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            rpcFeatureApi: FRpcFeatureApi,
        ): FDeviceColorFeatureApiImpl
    }

    companion object {
        private const val RPC_KEY_HARDWARE_COLOR = "hardware.color"
    }
}
