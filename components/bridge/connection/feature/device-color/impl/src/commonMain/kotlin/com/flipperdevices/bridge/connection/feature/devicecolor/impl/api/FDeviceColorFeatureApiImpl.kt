package com.flipperdevices.bridge.connection.feature.devicecolor.impl.api

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.devicecolor.api.FDeviceColorFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.property.GetRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class FDeviceColorFeatureApiImpl @AssistedInject constructor(
    @Assisted private val rpcFeatureApi: FRpcFeatureApi,
    private val settings: DataStore<PairSettings>
) : FDeviceColorFeatureApi,
    LogTagProvider {
    override val TAG = "FDeviceColorFeatureApi"

    override fun updateAndGetColorFlow(default: HardwareColor): Flow<HardwareColor> = flow {
        rpcFeatureApi.request(
            Main(
                property_get_request = GetRequest(
                    key = RPC_KEY_HARDWARE_COLOR
                )
            ).wrapToRequest()
        ).onEach { result ->
            val intValue = result.getOrNull()
                ?.property_get_response
                ?.value_
                ?.toIntOrNull()
            val hardwareColor = when (intValue) {
                HardwareColor.WHITE.value -> HardwareColor.WHITE
                HardwareColor.BLACK.value -> HardwareColor.BLACK
                else -> default
            }
            settings.updateData { pairSettings -> pairSettings.copy(hardware_color = hardwareColor) }
            emit(hardwareColor)
        }.collect()
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
