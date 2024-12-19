package com.flipperdevices.bridge.connection.feature.devicecolor.impl.api

import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroBleModel
import com.flipperdevices.bridge.connection.feature.devicecolor.api.FDeviceColorFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.api.FGetInfoFeatureApi
import com.flipperdevices.bridge.connection.feature.getinfo.model.FGetInfoApiProperty
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.preference.pb.FlipperZeroBle.HardwareColor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class FDeviceColorFeatureApiImpl @AssistedInject constructor(
    @Assisted private val fGetInfoFeatureApi: FGetInfoFeatureApi,
    private val fDevicePersistedStorage: FDevicePersistedStorage
) : FDeviceColorFeatureApi,
    LogTagProvider {
    override val TAG = "FDeviceColorFeatureApi"

    override fun updateAndGetColorFlow(default: HardwareColor): Flow<HardwareColor> = flow {
        val currentColoredDevice = fDevicePersistedStorage
            .getCurrentDevice()
            .first() as? FDeviceFlipperZeroBleModel
        if (currentColoredDevice != null) {
            emit(currentColoredDevice.hardwareColor)
        }

        val intHardwareColor = fGetInfoFeatureApi.get(FGetInfoApiProperty.DeviceInfo.HARDWARE_COLOR)
            .onFailure { error(
                it
            ) { "#updateAndGetColorFlow could not fetch ${FGetInfoApiProperty.DeviceInfo.HARDWARE_COLOR}" } }
            .map { it.toIntOrNull() }
            .getOrNull()

        val hardwareColor = when (intHardwareColor) {
            HardwareColor.WHITE.value -> HardwareColor.WHITE
            HardwareColor.BLACK.value -> HardwareColor.BLACK
            HardwareColor.TRANSPARENT.value -> HardwareColor.TRANSPARENT
            else -> default
        }

        currentColoredDevice?.uniqueId?.let { id ->
            fDevicePersistedStorage.updateDevice(id) { savedDevice ->
                savedDevice.copy(
                    flipper_zero_ble = savedDevice.flipper_zero_ble?.copy(hardware_color = hardwareColor)
                )
            }
        }
        emit(hardwareColor)
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            fGetInfoFeatureApi: FGetInfoFeatureApi,
        ): FDeviceColorFeatureApiImpl
    }
}
