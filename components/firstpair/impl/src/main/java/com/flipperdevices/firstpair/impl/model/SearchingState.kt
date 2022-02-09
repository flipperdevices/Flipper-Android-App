package com.flipperdevices.firstpair.impl.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.firstpair.impl.R

data class SearchingState(
    val showSearching: Boolean = false,
    val showHelp: Boolean = false,
    val content: SearchingContent
)

sealed class SearchingContent {
    object Searching : SearchingContent()

    data class FoundedDevices(
        val devices: List<DiscoveredBluetoothDevice>,
        val selectedAddress: String? = null
    ) : SearchingContent()

    class Finished(val deviceId: String?) :
        SearchingContent() // All work finished, exit from screen

    abstract class PermissionRequest(
        @DrawableRes val image: Int,
        @StringRes val title: Int,
        @StringRes val description: Int,
        @StringRes val buttonText: Int
    ) : SearchingContent() {
        fun onButtonClick() = Unit
    }

    object TurnOnBluetooth : PermissionRequest(
        image = R.drawable.pic_ble_disabled,
        title = R.string.firstpair_permission_enable_bluetooth_title,
        description = R.string.firstpair_permission_enable_bluetooth_desc,
        buttonText = R.string.firstpair_permission_continue
    )

    class BluetoothPermission(requestedFirstTime: Boolean) : PermissionRequest(
        image = R.drawable.pic_ble_permission_failed,
        title = R.string.firstpair_permission_bluetooth_title,
        description = R.string.firstpair_permission_bluetooth_desc,
        buttonText = if (requestedFirstTime) {
            R.string.firstpair_permission_continue
        } else R.string.firstpair_permission_settings
    )

    object TurnOnLocation : PermissionRequest(
        image = R.drawable.pic_turn_on_location,
        title = R.string.firstpair_permission_enable_location_title,
        description = R.string.firstpair_permission_location_desc,
        buttonText = R.string.firstpair_permission_settings
    )

    class LocationPermission(requestedFirstTime: Boolean) : PermissionRequest(
        image = R.drawable.pic_location_permission_failed,
        title = R.string.firstpair_permission_location_title,
        description = R.string.firstpair_permission_location_desc,
        buttonText = if (requestedFirstTime) {
            R.string.firstpair_permission_continue
        } else R.string.firstpair_permission_settings
    )

    object FlipperNotFound : PermissionRequest(
        image = R.drawable.pic_device_not_found,
        title = R.string.firstpair_device_not_found_title,
        description = R.string.firstpair_device_not_found_desc,
        buttonText = R.string.firstpair_permission_continue
    )
}
