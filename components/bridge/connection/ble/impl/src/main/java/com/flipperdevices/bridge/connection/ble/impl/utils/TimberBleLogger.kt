package com.flipperdevices.bridge.connection.ble.impl.utils

import android.util.Log
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.log.warn
import no.nordicsemi.android.common.logger.BleLoggerAndLauncher

class TimberBleLogger : BleLoggerAndLauncher, LogTagProvider {
    override val TAG = "BleLogger"

    override fun launch() {
        info { "Request launch" }
    }

    override fun log(priority: Int, log: String) {
        when (priority) {
            Log.ASSERT,
            Log.ERROR -> error { log }

            Log.WARN -> warn { log }
            Log.INFO -> info { log }
            Log.DEBUG -> debug { log }
            Log.VERBOSE -> verbose { log }
        }
    }
}
