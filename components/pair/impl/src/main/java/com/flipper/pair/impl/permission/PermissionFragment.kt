package com.flipper.pair.impl.permission

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.flipper.bridge.utils.DeviceFeatureHelper
import com.flipper.bridge.utils.PermissionHelper
import com.flipper.core.di.ComponentHolder
import com.flipper.core.utils.toast
import com.flipper.core.view.ComposeFragment
import com.flipper.pair.impl.R
import com.flipper.pair.impl.di.PairComponent
import com.flipper.pair.impl.navigation.machine.PairScreenStateDispatcher
import com.flipper.pair.impl.permission.compose.ComposePermission
import javax.inject.Inject

class PermissionFragment : ComposeFragment() {
    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    // Result listener for bluetooth toggle
    private val bluetoothEnableWithResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            toast(R.string.pair_permission_bt_enabled_failed)
            return@registerForActivityResult
        }
        requestPermissions()
    }

    // Result listener for permission request
    private val requestPermissionWithResult = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val containsNotGrantedPermission = permissions.entries.map { it.value }.contains(false)
        if (containsNotGrantedPermission) {
            toast(R.string.pair_permission_grant_failed)
            return@registerForActivityResult
        }
        onAllPermissionGranted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComponentHolder.component<PairComponent>().inject(this)

        // If we have companion feature, we just want enable bluetooth on this screen
        if (PermissionHelper.isBluetoothEnabled() &&
            (DeviceFeatureHelper.isCompanionFeatureAvailable(requireContext()) ||
                    PermissionHelper.isPermissionGranted(requireContext()))
        ) {
            onAllPermissionGranted()
            return
        }
        enableBluetoothAndRequestPermissions()
    }

    @Composable
    override fun renderView() {
        ComposePermission(requestPermissionButton = { enableBluetoothAndRequestPermissions() })
    }

    private fun enableBluetoothAndRequestPermissions() {
        if (!PermissionHelper.isBluetoothEnabled()) {
            bluetoothEnableWithResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            return
        }
        requestPermissions()
    }

    // Request permission which not grant already
    private fun requestPermissions() {
        val context = requireContext()
        // For companion feature we don't want request permission
        if (DeviceFeatureHelper.isCompanionFeatureAvailable(context)) {
            onAllPermissionGranted()
            return
        }
        val needPermissions: MutableList<String> = ArrayList()
        for (permissionName in PermissionHelper.getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permissionName
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needPermissions.add(permissionName)
            }
        }
        if (needPermissions.isEmpty()) {
            onAllPermissionGranted()
            return
        }
        requestPermissionWithResult.launch(needPermissions.toTypedArray())
    }

    // Navigate to next screen
    private fun onAllPermissionGranted() {
        stateDispatcher.invalidateCurrentState { it.copy(permissionGranted = true) }
    }
}
