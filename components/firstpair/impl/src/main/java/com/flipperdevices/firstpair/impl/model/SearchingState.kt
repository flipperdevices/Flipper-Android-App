package com.flipperdevices.firstpair.impl.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.firstpair.impl.R
import com.flipperdevices.firstpair.impl.viewmodels.SearchStateBuilder

data class SearchingState(
    val showSearching: Boolean = false,
    val showHelp: Boolean = false,
    val content: SearchingContent
)

sealed class SearchingContent {
    object Searching : SearchingContent()

    data class FoundedDevices(
        val devices: List<DiscoveredBluetoothDevice>,
        val pairState: DevicePairState
    ) : SearchingContent()

    class Finished(
        val deviceId: String?,
        val deviceName: String?
    ) : SearchingContent() // All work finished, exit from screen

    abstract class PermissionRequest(
        @DrawableRes val image: Int,
        @StringRes val title: Int,
        @StringRes val description: Int,
        @StringRes val buttonText: Int,
        private val searchStateHolder: SearchStateBuilder
    ) : SearchingContent() {
        open fun onButtonClick() {
            searchStateHolder.unfreezeInvalidate()
            searchStateHolder.invalidate()
        }
    }

    @Suppress("LongParameterList")
    abstract class PermissionRequestWithAppSettings(
        @DrawableRes image: Int,
        @StringRes title: Int,
        @StringRes description: Int,
        @StringRes buttonText: Int,
        private val searchStateHolder: SearchStateBuilder,
        private val context: Context,
        private val shouldInvalidateInsteadCallSettings: Boolean
    ) : PermissionRequest(image, title, description, buttonText, searchStateHolder) {
        override fun onButtonClick() {
            searchStateHolder.unfreezeInvalidate()
            if (shouldInvalidateInsteadCallSettings) {
                searchStateHolder.invalidate()
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(intent)
            }
        }
    }

    abstract class PermissionSettingsRequest(
        @DrawableRes image: Int,
        @StringRes title: Int,
        @StringRes description: Int,
        @StringRes buttonText: Int,
        private val searchStateHolder: SearchStateBuilder,
        private val context: Context
    ) : PermissionRequest(image, title, description, buttonText, searchStateHolder) {
        override fun onButtonClick() {
            searchStateHolder.unfreezeInvalidate()
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    class TurnOnBluetooth(searchStateHolder: SearchStateBuilder) : PermissionRequest(
        image = R.drawable.pic_ble_disabled,
        title = R.string.firstpair_permission_enable_bluetooth_title,
        description = R.string.firstpair_permission_enable_bluetooth_desc,
        buttonText = R.string.firstpair_permission_continue,
        searchStateHolder = searchStateHolder
    )

    class BluetoothPermission(
        searchStateHolder: SearchStateBuilder,
        context: Context,
        requestedFirstTime: Boolean
    ) : PermissionRequestWithAppSettings(
        image = R.drawable.pic_ble_permission_failed,
        title = R.string.firstpair_permission_bluetooth_title,
        description = R.string.firstpair_permission_bluetooth_desc,
        buttonText = if (requestedFirstTime) {
            R.string.firstpair_permission_continue
        } else {
            R.string.firstpair_permission_settings
        },
        searchStateHolder = searchStateHolder,
        context = context,
        shouldInvalidateInsteadCallSettings = requestedFirstTime
    )

    class TurnOnLocation(
        searchStateHolder: SearchStateBuilder,
        context: Context
    ) : PermissionSettingsRequest(
        image = R.drawable.pic_location_permission,
        title = R.string.firstpair_permission_enable_location_title,
        description = R.string.firstpair_permission_location_desc,
        buttonText = R.string.firstpair_permission_settings,
        searchStateHolder = searchStateHolder,
        context = context
    )

    class LocationPermission(
        searchStateHolder: SearchStateBuilder,
        context: Context,
        requestedFirstTime: Boolean
    ) : PermissionRequestWithAppSettings(
        image = R.drawable.pic_location_permission,
        title = R.string.firstpair_permission_location_title,
        description = R.string.firstpair_permission_location_desc,
        buttonText = if (requestedFirstTime) {
            R.string.firstpair_permission_continue
        } else {
            R.string.firstpair_permission_settings
        },
        searchStateHolder = searchStateHolder,
        context = context,
        shouldInvalidateInsteadCallSettings = requestedFirstTime
    )

    class FlipperNotFound(
        private val searchStateHolder: SearchStateBuilder
    ) : PermissionRequest(
        image = R.drawable.pic_device_not_found,
        title = R.string.firstpair_device_not_found_title,
        description = R.string.firstpair_device_not_found_desc,
        buttonText = R.string.firstpair_permission_retry,
        searchStateHolder = searchStateHolder
    ) {
        override fun onButtonClick() {
            searchStateHolder.resetByUser()
        }
    }
}
