package com.flipperdevices.pair.impl.fragments.findcompanion

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Context
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.preference.FlipperSharedPreferences
import com.flipperdevices.core.preference.FlipperSharedPreferencesKey
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.pair.impl.R
import com.flipperdevices.pair.impl.composable.findcompanion.ComposeFindDevice
import com.flipperdevices.pair.impl.di.PairComponent
import com.flipperdevices.pair.impl.findstandart.service.PairDeviceViewModel
import com.flipperdevices.pair.impl.fragments.common.BluetoothEnableHelper
import com.flipperdevices.pair.impl.model.findcompanion.PairingState
import com.flipperdevices.pair.impl.navigation.machine.PairScreenStateDispatcher
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class CompanionFindFragment : ComposeFragment(), LogTagProvider {
    override val TAG = "CompanionFindFragment"

    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    @Inject
    lateinit var sharedPreferences: FlipperSharedPreferences

    private val pairDeviceViewModel by viewModels<PairDeviceViewModel>()

    private val bluetoothEnableHelper = BluetoothEnableHelper(this)

    // Result listener for device pair
    private val deviceConnectWithResult = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            pairDeviceViewModel.onFailedCompanionFinding(
                getString(R.string.pair_companion_error_return_not_ok)
            )
            return@registerForActivityResult
        }
        val deviceToPair: BluetoothDevice? =
            result.data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
        if (deviceToPair == null) {
            pairDeviceViewModel.onFailedCompanionFinding(
                getString(R.string.pair_companion_error_return_device_null)
            )
            return@registerForActivityResult
        }

        // Store founded device in memory
        pairDeviceViewModel.onDeviceFounded(deviceToPair)

        // After Android 12 we need permission BLUETOOTH_CONNECT on this step
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestConnectPermissionResult.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            pairDeviceViewModel.startConnectToDevice {
                onDeviceReady(it)
            }
        }
    }

    private val requestConnectPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (!permissionGranted) {
            pairDeviceViewModel.onFailedCompanionFinding(
                getString(R.string.pair_companion_error_permission)
            )
            return@registerForActivityResult
        }
        pairDeviceViewModel.startConnectToDevice {
            onDeviceReady(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<PairComponent>().inject(this)
    }

    override fun onResume() {
        super.onResume()
        val connectionState = pairDeviceViewModel.getConnectionState().value
        // Don't open dialog if we already communicate with device
        if (connectionState !is PairingState.WithDevice &&
            connectionState != PairingState.FindingDevice
        ) {
            requestPermissionAndOpenFindDeviceDialog()
        }
    }

    @Composable
    override fun renderView() {
        val connectionState by pairDeviceViewModel.getConnectionState().collectAsState()
        ComposeFindDevice(
            connectionState,
            onClickBackButton = { stateDispatcher.back() }
        ) {
            requestPermissionAndOpenFindDeviceDialog()
        }
    }

    private fun requestPermissionAndOpenFindDeviceDialog() {
        bluetoothEnableHelper.requestBluetoothEnable {
            openFindDeviceDialog()
        }
    }

    private fun openFindDeviceDialog() {
        EnableLocationDialogHelper.showDialogIfLocationDisabled(requireContext())

        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            // Match only Bluetooth devices whose name matches the pattern.
            .setNamePattern(Constants.DEVICENAME_PREFIX_REGEXP)
            .build()
        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            // Find only devices that match this request filter.
            .addDeviceFilter(deviceFilter)
            .build()
        val deviceManager = requireContext().getSystemService(Context.COMPANION_DEVICE_SERVICE)
            as CompanionDeviceManager
        pairDeviceViewModel.onStartCompanionFinding()

        deviceManager.associate(
            pairingRequest,
            object : CompanionDeviceManager.Callback() {
                override fun onDeviceFound(chooserLauncher: IntentSender?) {
                    if (chooserLauncher == null) {
                        return
                    }
                    val intentSenderRequest = IntentSenderRequest.Builder(chooserLauncher).build()
                    deviceConnectWithResult.launch(intentSenderRequest)
                }

                override fun onFailure(error: CharSequence) {
                    val errorText = getString(R.string.pair_companion_error_try_again)
                    pairDeviceViewModel.onFailedCompanionFinding(errorText)
                    error { error.toString() }
                }
            },
            null
        )
    }

    private fun onDeviceReady(device: BluetoothDevice) {
        sharedPreferences.edit { putString(FlipperSharedPreferencesKey.DEVICE_ID, device.address) }
        stateDispatcher.invalidateCurrentState { it.copy(devicePaired = true) }
    }
}
