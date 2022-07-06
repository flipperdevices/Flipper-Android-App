package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.info.impl.di.InfoComponent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlipperColorViewModel : ViewModel() {
    private val colorFlipperState = MutableStateFlow(HardwareColor.WHITE)

    @Inject
    lateinit var settings: DataStore<PairSettings>

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
        settings.data.onEach {
            colorFlipperState.emit(it.hardwareColor)
        }.launchIn(viewModelScope)
    }

    fun getFlipperColor(): StateFlow<HardwareColor> = colorFlipperState
}
