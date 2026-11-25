package com.flipperdevices.bridge.connection.transport.usb.impl.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class USBPermissionReceiver(
    private val onPermissionResponse: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        onPermissionResponse()
    }
}
