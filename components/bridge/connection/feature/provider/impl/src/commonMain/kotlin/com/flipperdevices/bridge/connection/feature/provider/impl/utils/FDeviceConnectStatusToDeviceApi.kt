package com.flipperdevices.bridge.connection.feature.provider.impl.utils

import com.flipperdevices.bridge.connection.config.api.FDeviceType
import com.flipperdevices.bridge.connection.device.common.api.FDeviceApi
import com.flipperdevices.bridge.connection.device.fzero.api.FZeroDeviceApi
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import javax.inject.Inject

class FDeviceConnectStatusToDeviceApi @Inject constructor(
    private val fZeroDeviceApiFactory: FZeroDeviceApi.Factory
) {
    fun get(status: FDeviceConnectStatus.Connected): FDeviceApi {
        return when (status.device.type) {
            FDeviceType.FLIPPER_ZERO_BLE -> fZeroDeviceApiFactory(
                scope = status.scope,
                connectedDevice = status.deviceApi
            )
        }
    }
}
