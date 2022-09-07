package com.flipperdevices.keyscreen.emulate.viewmodel

import android.app.Application
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.keyscreen.emulate.viewmodel.helpers.EmulateHelper
import tangle.viewmodel.VMInject

class SimpleEmulateViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    emulateHelper: EmulateHelper,
    synchronizationApi: SynchronizationApi,
    application: Application
) : EmulateViewModel(serviceProvider, emulateHelper, synchronizationApi, application) {
    override val TAG = "SimpleEmulateViewModel"
}
