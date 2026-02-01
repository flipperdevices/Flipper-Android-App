package com.flipperdevices.bridge.connection.feature.networkproxy.impl.api

actual object NetworkProxyServiceController {
    actual fun startService() {
        // No-op on desktop - no foreground service needed
    }

    actual fun stopService() {
        // No-op on desktop
    }
}
