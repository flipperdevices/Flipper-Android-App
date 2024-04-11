package com.flipperdevices.bridge.connection.screens.benchmark

import com.flipperdevices.bridge.connection.ble.api.FBleDeviceConnectionConfig
import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.ble.api.OverflowControlConfig
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.util.UUID

class BenchmarkViewModel @AssistedInject constructor(
    @Assisted address: String,
    private val deviceOrchestrator: FDeviceOrchestrator
) : DecomposeViewModel() {
    init {
        viewModelScope.launch {
            deviceOrchestrator.connect(
                FBleDeviceConnectionConfig(
                    macAddress = address,
                    serialConfig = FBleDeviceSerialConfig(
                        serialServiceUuid = UUID.fromString("8fe5b3d5-2e7f-4a98-2a48-7acc60fe0000"),
                        rxServiceCharUuid = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e61fe0000"),
                        txServiceCharUuid = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e62fe0000"),
                        resetServiceUUID = UUID.fromString("19ed82ae-ed21-4c9d-4145-228e64fe0000"),
                        overflowControl = OverflowControlConfig(
                            UUID.fromString("19ed82ae-ed21-4c9d-4145-228e63fe0000")
                        )
                    )
                )
            )
        }
    }

    fun getState() = deviceOrchestrator.getState()

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted address: String
        ): BenchmarkViewModel
    }
}
