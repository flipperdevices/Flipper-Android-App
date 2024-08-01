package com.flipperdevices.info.impl.viewmodel

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.preference.pb.PairSettings
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.property.getRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val RPC_KEY_HARDWARE_COLOR = "hardware.color"

class FlipperColorViewModel @Inject constructor(
    private val settings: DataStore<PairSettings>
) : DecomposeViewModel(), FlipperBleServiceConsumer {
    private val colorFlipperState = MutableStateFlow<HardwareColor>(HardwareColor.fromValue(-1))

    init {
        settings.data.onEach {
            colorFlipperState.emit(it.hardware_color)
        }.launchIn(viewModelScope)
    }

    fun getFlipperColor(): StateFlow<HardwareColor> = colorFlipperState

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        viewModelScope.launch {
            if (!serviceApi.flipperVersionApi.isSupported(Constants.API_SUPPORTED_GET_REQUEST)) {
                return@launch
            }
            val response = serviceApi.requestApi.request(
                flowOf(
                    main {
                        propertyGetRequest = getRequest {
                            key = RPC_KEY_HARDWARE_COLOR
                        }
                    }.wrapToRequest(FlipperRequestPriority.BACKGROUND)
                )
            )
            if (response.hasPropertyGetResponse().not()) {
                return@launch
            }
            val hardwareColor = when (response.propertyGetResponse.value.toIntOrNull()) {
                HardwareColor.WHITE.value -> HardwareColor.WHITE
                HardwareColor.BLACK.value -> HardwareColor.BLACK
                else -> HardwareColor.WHITE
            }
            settings.updateData {
                it.copy(
                    hardware_color = hardwareColor
                )
            }
        }
    }
}
