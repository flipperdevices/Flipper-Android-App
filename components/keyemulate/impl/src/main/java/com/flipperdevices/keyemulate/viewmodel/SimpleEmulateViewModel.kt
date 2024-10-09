package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
import javax.inject.Inject

class SimpleEmulateViewModel @Inject constructor(
    serviceProvider: FlipperServiceProvider,
    emulateHelper: EmulateHelper,
    synchronizationApi: SynchronizationApi,
    closeEmulateAppTaskHolder: CloseEmulateAppTaskHolder,
    application: Application,
    settings: DataStore<Settings>
) : EmulateViewModel(
    serviceProvider,
    emulateHelper,
    synchronizationApi,
    closeEmulateAppTaskHolder,
    application,
    settings
) {
    override val TAG = "SimpleEmulateViewModel"
}
