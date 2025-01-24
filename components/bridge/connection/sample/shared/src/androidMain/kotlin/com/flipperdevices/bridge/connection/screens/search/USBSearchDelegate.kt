package com.flipperdevices.bridge.connection.screens.search

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroUsbModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.hoho.android.usbserial.driver.UsbSerialProber
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
    private val context: Context,
    private val persistedStorage: FDevicePersistedStorage
) : ConnectionSearchDelegate, LogTagProvider {
    override val TAG = "USBSearchViewModel"

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

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
                UsbSerialProber
                    .getDefaultProber()
                    .findAllDrivers(usbManager)
                    .map { it.device } to savedDevices
            }.collect { (searchDevices, savedDevices) ->
                val existedDescriptors = savedDevices
                    .filterIsInstance<FDeviceFlipperZeroUsbModel>()
                    .associateBy { it.portPath }

                info { searchDevices.joinToString(",") { "$it" } }

                searchItems.emit(
                    searchDevices.map { it.toFDeviceFlipperZeroUSBModel() }.map { usbDevice ->
                        ConnectionSearchItem(
                            address = usbDevice.portPath,
                            deviceModel = existedDescriptors[usbDevice.portPath]
                                ?: usbDevice,
                            isAdded = existedDescriptors.containsKey(usbDevice.portPath)
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

private fun UsbDevice.toFDeviceFlipperZeroUSBModel(): FDeviceFlipperZeroUsbModel {
    return FDeviceFlipperZeroUsbModel(
        name = productName?.extractFlipperName() ?: deviceName,
        portPath = deviceId.toString(),
        humanReadableName = productName ?: deviceName,
    )
}

private fun String.extractFlipperName(): String {
    val regexFind = FLIPPER_NAME_REGEXP.find(this)
    val groups = regexFind?.groupValues
    return groups?.getOrNull(index = 1)
        ?: this
}
