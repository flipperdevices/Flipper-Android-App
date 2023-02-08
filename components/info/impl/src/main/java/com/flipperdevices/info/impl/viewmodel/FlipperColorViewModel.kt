package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.preference.pb.PairSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tangle.viewmodel.VMInject

class FlipperColorViewModel @VMInject constructor(
    settings: DataStore<PairSettings>
) : ViewModel() {
    private val colorFlipperState = MutableStateFlow(HardwareColor.WHITE)

    init {
        settings.data.onEach {
            colorFlipperState.emit(it.hardwareColor)
        }.launchIn(viewModelScope)
    }

    fun getFlipperColor(): StateFlow<HardwareColor> = colorFlipperState
}
