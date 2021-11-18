package com.flipperdevices.pair.impl.fragments.permission

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.flipperdevices.bridge.api.utils.DeviceFeatureHelper
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.pair.impl.R
import com.flipperdevices.pair.impl.composable.permission.ComposePermission
import com.flipperdevices.pair.impl.di.PairComponent
import com.flipperdevices.pair.impl.fragments.common.BluetoothEnableHelper
import com.flipperdevices.pair.impl.navigation.machine.PairScreenStateDispatcher
import javax.inject.Inject

class PermissionFragment : ComposeFragment() {
    @Inject
    lateinit var stateDispatcher: PairScreenStateDispatcher

    private val bluetoothEnableHelper = BluetoothEnableHelper(this)

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
            (
                DeviceFeatureHelper.isCompanionFeatureAvailable(requireContext()) ||
                    PermissionHelper.isPermissionGranted(requireContext())
                )
        ) {
            onAllPermissionGranted()
            return
        }
        enableBluetoothAndRequestPermissions()
    }

    @Composable
    override fun RenderView() {
        ComposePermission(requestPermissionButton = { enableBluetoothAndRequestPermissions() })
    }

    private fun enableBluetoothAndRequestPermissions() {
        bluetoothEnableHelper.requestBluetoothEnable {
            requestPermissions()
        }
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
