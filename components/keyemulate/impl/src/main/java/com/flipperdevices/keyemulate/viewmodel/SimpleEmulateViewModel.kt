package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import tangle.viewmodel.VMInject

class SimpleEmulateViewModel @VMInject constructor(
    serviceProvider: FlipperServiceProvider,
    emulateHelper: EmulateHelper,
    synchronizationApi: SynchronizationApi,
    application: Application,
    screenStreamingEntry: ScreenStreamingFeatureEntry,
) : EmulateViewModel(serviceProvider, emulateHelper, synchronizationApi, screenStreamingEntry, application) {
    override val TAG = "SimpleEmulateViewModel"
}
