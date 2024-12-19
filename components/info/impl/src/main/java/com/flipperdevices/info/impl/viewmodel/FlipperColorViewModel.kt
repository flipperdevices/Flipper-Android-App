package com.flipperdevices.info.impl.viewmodel

import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroBleModel
import com.flipperdevices.core.preference.pb.FlipperZeroBle.HardwareColor
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class FlipperColorViewModel @Inject constructor(
    fDevicePersistedStorage: FDevicePersistedStorage,
) : DecomposeViewModel() {
    private val colorFlipperState = fDevicePersistedStorage.getCurrentDevice()
        .filterIsInstance<FDeviceFlipperZeroBleModel>()
        .map { coloredDevice -> coloredDevice.hardwareColor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, HardwareColor.fromValue(-1))

    fun getFlipperColor(): StateFlow<HardwareColor> = colorFlipperState
}
