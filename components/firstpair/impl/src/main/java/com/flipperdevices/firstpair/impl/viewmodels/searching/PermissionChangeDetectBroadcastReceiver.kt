package com.flipperdevices.firstpair.impl.viewmodels.searching

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import com.flipperdevices.firstpair.impl.viewmodels.SearchStateBuilder

class PermissionChangeDetectBroadcastReceiver(
    private val searchStateBuilder: SearchStateBuilder
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        searchStateBuilder.unfreezeInvalidate()
        searchStateBuilder.invalidate()
    }

    fun register(context: Context) {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)

        context.registerReceiver(this@PermissionChangeDetectBroadcastReceiver, filter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }
}
