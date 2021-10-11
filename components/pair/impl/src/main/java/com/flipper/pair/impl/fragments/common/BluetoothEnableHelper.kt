package com.flipper.pair.impl.fragments.common

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.flipper.bridge.api.utils.PermissionHelper
import com.flipper.core.utils.toast
import com.flipper.pair.impl.R

class BluetoothEnableHelper(
    private val fragment: Fragment,
) {
    private var onBluetoothEnabled: (() -> Unit)? = null

    // Result listener for bluetooth toggle
    private val bluetoothEnableWithResult = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode != Activity.RESULT_OK) {
            fragment.toast(R.string.pair_permission_bt_enabled_failed)
        }
        requestBluetoothEnabledInternal()
    }

    fun requestBluetoothEnable(onBluetoothEnabled: () -> Unit) {
        this.onBluetoothEnabled = onBluetoothEnabled
        requestBluetoothEnabledInternal()
    }

    private fun requestBluetoothEnabledInternal() {
        if (!PermissionHelper.isBluetoothEnabled()) {
            bluetoothEnableWithResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            return
        }
        onBluetoothEnabled?.invoke()
    }
}
