package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.devicecolor.api.FDeviceColorFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FlipperColorViewModel @Inject constructor(
    private val settings: DataStore<PairSettings>,
    fFeatureProvider: FFeatureProvider
) : DecomposeViewModel() {
    private val colorFlipperState = MutableStateFlow(HardwareColor.fromValue(-1))

    fun getFlipperColor(): StateFlow<HardwareColor> = colorFlipperState

    init {
        settings.data.onEach {
            colorFlipperState.emit(it.hardware_color)
        }.launchIn(viewModelScope)

        fFeatureProvider.get<FDeviceColorFeatureApi>()
            .filterIsInstance<FFeatureStatus.Supported<FDeviceColorFeatureApi>>()
            .flatMapLatest { status -> status.featureApi.getColor() }
            .onEach { hardwareColor ->
                settings.updateData { data ->
                    data.copy(hardware_color = hardwareColor)
                }
            }.launchIn(viewModelScope)
    }
}
