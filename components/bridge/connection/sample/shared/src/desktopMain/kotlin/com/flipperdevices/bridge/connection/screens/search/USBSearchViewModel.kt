package com.flipperdevices.bridge.connection.screens.search

import com.fazecast.jSerialComm.SerialPort
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroUsbModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private val FLIPPER_NAME_REGEXP = "Flipper ([A-Za-z]+)".toRegex()

@ContributesBinding(AppGraph::class, ConnectionSearchViewModel::class)
class USBSearchViewModel @Inject constructor(
    private val persistedStorage: FDevicePersistedStorage
) : ConnectionSearchViewModel(persistedStorage), LogTagProvider {
    override val TAG = "USBSearchViewModel"

    private val searchItems =
        MutableStateFlow<ImmutableList<ConnectionSearchItem>>(persistentListOf())

    init {
        viewModelScope.launch {
            combine(
                flow {
                    while (true) {
                        emit(Unit)
                        delay(1.toDuration(DurationUnit.SECONDS))
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
