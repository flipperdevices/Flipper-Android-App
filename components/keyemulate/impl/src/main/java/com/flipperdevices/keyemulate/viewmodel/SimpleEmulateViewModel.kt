package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
import javax.inject.Inject

class SimpleEmulateViewModel @Inject constructor(
    serviceProvider: FlipperServiceProvider,
    emulateHelper: EmulateHelper,
    synchronizationApi: SynchronizationApi,
    closeEmulateAppTaskHolder: CloseEmulateAppTaskHolder,
    application: Application,
) : EmulateViewModel(
    serviceProvider,
    emulateHelper,
    synchronizationApi,
    closeEmulateAppTaskHolder,
    application
) {
    override val TAG = "SimpleEmulateViewModel"
}
