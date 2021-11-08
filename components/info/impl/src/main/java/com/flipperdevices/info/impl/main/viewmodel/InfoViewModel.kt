package com.flipperdevices.info.impl.main.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceConsumer
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.LifecycleViewModel
import com.flipperdevices.info.impl.di.InfoComponent
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.file
import com.flipperdevices.protobuf.storage.writeRequest
import com.google.protobuf.ByteString
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.ktx.state.ConnectionState

private const val BRUTEFORCE_POCKET_COUNT = 1000
private const val BRUTEFORCE_POCKET_SIZE = 500

class InfoViewModel : LifecycleViewModel(), FlipperBleServiceConsumer {
    @Inject
    lateinit var bleService: FlipperServiceProvider

    private val informationState = MutableStateFlow(FlipperGATTInformation())
    private val connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnecting)

    init {
        ComponentHolder.component<InfoComponent>().inject(this)
        bleService.provideServiceApi(consumer = this, lifecycleOwner = this)
    }

    fun getDeviceInformation(): StateFlow<FlipperGATTInformation> {
        return informationState
    }

    fun getConnectionState(): StateFlow<ConnectionState> {
        return connectionState
    }

    fun emitManyPocketToFlipper() {
        bleService.provideServiceApi(this) { serviceApi ->
            viewModelScope.launch {
                repeat(BRUTEFORCE_POCKET_COUNT) {
                    async {
                        serviceApi.requestApi.request(
                            main {
                                storageWriteRequest = writeRequest {
                                    path = "/ext/empty"
                                    file = file {
                                        data = ByteString.copyFrom(
                                            ByteArray(BRUTEFORCE_POCKET_SIZE)
                                        )
                                    }
                                }
                            }.wrapToRequest()
                        ).collect { }
                    }
                }
            }
        }
    }

    override fun onServiceApiReady(serviceApi: FlipperServiceApi) {
        serviceApi.flipperInformationApi.getInformationFlow().onEach {
            informationState.emit(it)
        }.launchIn(viewModelScope)
        serviceApi.connectionInformationApi.getConnectionStateFlow().onEach {
            connectionState.emit(it)
        }.launchIn(viewModelScope)
    }
}
