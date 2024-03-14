package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.ble.api.FBleApi
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

class FBleApiImpl(
    private val scope: CoroutineScope,
    private val client: ClientBleGatt
) : FBleApi {

}