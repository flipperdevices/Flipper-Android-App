package com.flipperdevices.bridge.connection.feature.devicecolor.impl.api

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.devicecolor.api.FDeviceColorFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.preference.pb.PairSettings
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FDeviceColorFeatureApiImpl @AssistedInject constructor(
    @Assisted private val fGetInfoFeatureApi: FGetInfoFeatureApi,
    private val settings: DataStore<PairSettings>
) : FDeviceColorFeatureApi,
    LogTagProvider {
    override val TAG = "FDeviceColorFeatureApi"

    override fun updateAndGetColorFlow(default: HardwareColor): Flow<HardwareColor> = flow {
        val savedHardwareColor = settings.data.map { it.hardware_color }
            .firstOrNull()
            ?.also { initialColor -> emit(initialColor) }
        val intHardwareColor = fGetInfoFeatureApi.get(FGetInfoApiProperty.DeviceInfo.HARDWARE_COLOR)
            .onFailure { error(it) { "#updateAndGetColorFlow could not fetch ${FGetInfoApiProperty.DeviceInfo.HARDWARE_COLOR}" } }
            .map { it.toIntOrNull() }
            .getOrDefault(savedHardwareColor?.value)

        val hardwareColor = when (intHardwareColor) {
            HardwareColor.WHITE.value -> HardwareColor.WHITE
            HardwareColor.BLACK.value -> HardwareColor.BLACK
            else -> default
        }
        settings.updateData { pairSettings -> pairSettings.copy(hardware_color = hardwareColor) }
        emit(hardwareColor)
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            fGetInfoFeatureApi: FGetInfoFeatureApi,
        ): FDeviceColorFeatureApiImpl
    }
}
