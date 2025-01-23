package com.flipperdevices.bridge.connection.screens.search

import com.fazecast.jSerialComm.SerialPort
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroUsbModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesMultibinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private val FLIPPER_NAME_REGEXP = "Flipper ([A-Za-z]+)".toRegex()

class USBSearchDelegate @AssistedInject constructor(
    @Assisted viewModelScope: CoroutineScope,
    private val persistedStorage: FDevicePersistedStorage
) : ConnectionSearchDelegate, LogTagProvider {
    override val TAG = "USBSearchViewModel"

    private val searchItems =
        MutableStateFlow<ImmutableList<ConnectionSearchItem>>(persistentListOf())

    init {
        viewModelScope.launch {
            combine(
                flow {
                    while (true) {
                        emit(Unit)
                        kotlinx.coroutines.delay(1.seconds)
                    }
                },
                persistedStorage.getAllDevices()
            ) { _, savedDevices ->
                SerialPort.getCommPorts() to savedDevices
            }.collect { (searchDevices, savedDevices) ->
                val existedDescriptors = savedDevices
                    .filterIsInstance<FDeviceFlipperZeroUsbModel>()
                    .associateBy { it.portPath }

                info { searchDevices.joinToString(",") { "${it.systemPortPath} (${it.portDescription})" } }

                val filteredSearchDevices =
                    searchDevices.filter { it.manufacturer == "Flipper Devices Inc." }

                searchItems.emit(
                    filteredSearchDevices.map { usbDevice ->
                        ConnectionSearchItem(
                            address = usbDevice.systemPortPath,
                            deviceModel = existedDescriptors[usbDevice.systemPortPath]
                                ?: usbDevice.toFDeviceFlipperZeroUSBModel(),
                            isAdded = existedDescriptors.containsKey(usbDevice.systemPortPath)
                        )
                    }.distinctBy { it.address }
                        .toImmutableList()

                )
            }
        }
    }

    override fun getDevicesFlow() = searchItems.asStateFlow()


    @AssistedFactory
    @ContributesMultibinding(AppGraph::class, ConnectionSearchDelegate.Factory::class)
    fun interface Factory : ConnectionSearchDelegate.Factory {
        override fun invoke(scope: CoroutineScope): USBSearchDelegate
    }
}

private fun SerialPort.toFDeviceFlipperZeroUSBModel(): FDeviceFlipperZeroUsbModel {
    val regexFind = FLIPPER_NAME_REGEXP.find(descriptivePortName)
    val groups = regexFind?.groupValues
    val name = groups?.getOrNull(index = 1)
        ?: descriptivePortName
    return FDeviceFlipperZeroUsbModel(
        name = name,
        portPath = systemPortPath,
        humanReadableName = descriptivePortName,
    )
}
