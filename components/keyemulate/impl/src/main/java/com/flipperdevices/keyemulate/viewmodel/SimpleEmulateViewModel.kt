package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.keyemulate.api.EmulateHelper
import javax.inject.Inject

class SimpleEmulateViewModel @Inject constructor(
    serviceProvider: FlipperServiceProvider,
    emulateHelper: EmulateHelper,
    synchronizationApi: SynchronizationApi,
    application: Application,
) : EmulateViewModel(serviceProvider, emulateHelper, synchronizationApi, application) {
    override val TAG = "SimpleEmulateViewModel"
}
