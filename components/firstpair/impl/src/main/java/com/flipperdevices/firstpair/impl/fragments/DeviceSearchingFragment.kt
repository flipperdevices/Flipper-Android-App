package com.flipperdevices.firstpair.impl.fragments

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.fragment.app.viewModels
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.ComposeFragment
import com.flipperdevices.firstpair.impl.composable.searching.ComposableSearchingScreen
import com.flipperdevices.firstpair.impl.di.FirstPairComponent
import com.flipperdevices.firstpair.impl.fragments.permissions.BluetoothEnableHelper
import com.flipperdevices.firstpair.impl.fragments.permissions.LocationEnableHelper
import com.flipperdevices.firstpair.impl.fragments.permissions.PermissionEnableHelper
import com.flipperdevices.firstpair.impl.viewmodels.searching.BLEDeviceViewModel
import javax.inject.Inject
import javax.inject.Provider

class DeviceSearchingFragment :
    ComposeFragment(),
    LogTagProvider,
    BluetoothEnableHelper.Listener,
    LocationEnableHelper.Listener,
    PermissionEnableHelper.Listener {
    override val TAG = "DeviceSearchingFragment"

    @Inject
    lateinit var bluetoothAdapterProvider: Provider<BluetoothAdapter>

    private var bluetoothEnableHelper: BluetoothEnableHelper? = null
    private var locationEnableHelper: LocationEnableHelper? = null
    private var permissionEnableHelper: PermissionEnableHelper? = null

    private val viewModelSearch by viewModels<BLEDeviceViewModel>()

    init {
        ComponentHolder.component<FirstPairComponent>().inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bluetoothEnableHelper = BluetoothEnableHelper(
            fragment = this,
            bluetoothAdapter = bluetoothAdapterProvider.get(),
            listener = this
        )
        locationEnableHelper = LocationEnableHelper(
            context = context,
            listener = this
        )
        permissionEnableHelper = PermissionEnableHelper(
            fragment = this,
            context = context,
            listener = this,
            permissions = PermissionHelper.getRequiredPermissions()
        )
    }

    @Composable
    override fun RenderView() {
        ComposableSearchingScreen(viewModelSearch)
    }

    private fun invalidate() {
        val bluetoothEnableHelperNotNull = bluetoothEnableHelper
        val locationEnableHelperNotNull = locationEnableHelper
        val permissionEnableHelperNotNull = permissionEnableHelper

        if (bluetoothEnableHelperNotNull == null ||
            locationEnableHelperNotNull == null ||
            permissionEnableHelperNotNull == null
        ) {
            error(RuntimeException()) { "Call invalidate before onAttach. Skip method call" }
            return
        }

        if (!bluetoothEnableHelperNotNull.isBluetoothEnabled()) {
            bluetoothEnableHelperNotNull.requestBluetoothEnable()
            return
        }

        if (!locationEnableHelperNotNull.isLocationEnabled()) {
            locationEnableHelperNotNull.requestLocationEnabled()
            return
        }

        if (!permissionEnableHelperNotNull.isPermissionGranted()) {
            permissionEnableHelperNotNull.requestPermissions()
            return
        }

        info { "All permission granted, start scan" }

        viewModelSearch.startScanIfNotYet()
    }

    override fun onResume() {
        super.onResume()
        invalidate()
    }

    override fun onBluetoothEnabled() {
        invalidate()
    }

    override fun onBluetoothUserDenied() {
        invalidate()
    }

    override fun onLocationEnabled() {
        invalidate()
    }

    override fun onLocationUserDenied() {
        invalidate()
    }

    override fun onPermissionGranted(permissions: Array<String>) {
        invalidate()
    }

    override fun onPermissionUserDenied(permissions: Array<String>) {
        invalidate()
    }
}
