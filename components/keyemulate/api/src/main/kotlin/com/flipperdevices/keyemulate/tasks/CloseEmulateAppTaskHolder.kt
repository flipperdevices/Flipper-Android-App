package com.flipperdevices.keyemulate.tasks

import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.keyemulate.api.EmulateHelper

interface CloseEmulateAppTaskHolder {
    fun closeEmulateApp(serviceProvider: FlipperServiceProvider, emulateHelper: EmulateHelper)
}
