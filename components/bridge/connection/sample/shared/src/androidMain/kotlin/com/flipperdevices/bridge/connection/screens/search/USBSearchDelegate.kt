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
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.binding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

private val FLIPPER_NAME_REGEXP = "Flipper ([A-Za-z]+)".toRegex()
private const val FLIPPER_USB_MANUFACTURER = "Flipper Devices Inc."

class USBSearchDelegate @AssistedInject constructor(
    @Assisted scope: CoroutineScope,
    private val context: Context,
    private val persistedStorage: FDevicePersistedStorage
) : ConnectionSearchDelegate, LogTagProvider {
    override val TAG = "USBSearchDelegate"

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private val usbSerialProber = UsbSerialProber.getDefaultProber()

    private val searchItems =
        MutableStateFlow<ImmutableList<ConnectionSearchItem>>(persistentListOf())

    init {
        scope.launch {
            combine(
                flow {
                    while (true) {
                        emit(Unit)
                        delay(1.seconds)
                    }
                },
                persistedStorage.getAllDevices()
            ) { _, savedDevices -> savedDevices }
                .collect { savedDevices ->
                    val existedDescriptors = savedDevices
                        .filterIsInstance<FDeviceFlipperZeroUsbModel>()
                        .associateBy { savedDevice -> savedDevice.portPath }

                    val searchDevices = usbSerialProber
                        .findAllDrivers(usbManager)
                        .map { serialDriver -> serialDriver.device }
                        .filter { usbDevice -> usbDevice.manufacturerName == FLIPPER_USB_MANUFACTURER }

                    info { searchDevices.joinToString(",") { usbDevice -> "$usbDevice" } }

                    searchItems.emit(
                        searchDevices.map { usbDevice ->
                            val usbModel = usbDevice.toFDeviceFlipperZeroUSBModel()
                            ConnectionSearchItem(
                                address = usbModel.portPath,
                                deviceModel = existedDescriptors[usbModel.portPath]
                                    ?: usbModel,
                                isAdded = existedDescriptors.containsKey(usbModel.portPath)
                            )
                        }.distinctBy { searchItem -> searchItem.address }
                            .toImmutableList()
                    )
                }
        }
    }

    override fun getDevicesFlow() = searchItems.asStateFlow()

    @AssistedFactory
    @ContributesIntoSet(AppGraph::class, binding<ConnectionSearchDelegate.Factory>())
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
