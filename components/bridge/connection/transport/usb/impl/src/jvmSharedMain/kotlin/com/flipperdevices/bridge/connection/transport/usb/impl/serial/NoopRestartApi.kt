package com.flipperdevices.bridge.connection.transport.usb.impl.serial

import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error

class NoopRestartApi : FSerialRestartApi, LogTagProvider {
    override val TAG = "NoopRestartApi"

    override suspend fun restartRpc() {
        error { "Restart rpc failed, because this is noop implementation" }
    }
}