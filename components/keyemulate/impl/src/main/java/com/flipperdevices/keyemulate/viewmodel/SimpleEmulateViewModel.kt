package com.flipperdevices.keyemulate.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.keyemulate.tasks.CloseEmulateAppTaskHolder
import javax.inject.Inject

class SimpleEmulateViewModel @Inject constructor(
    synchronizationApi: SynchronizationApi,
    closeEmulateAppTaskHolder: CloseEmulateAppTaskHolder,
    application: Application,
    settings: DataStore<Settings>,
    fFeatureProvider: FFeatureProvider,
    fDeviceOrchestrator: FDeviceOrchestrator
) : EmulateViewModel(
    synchronizationApi,
    closeEmulateAppTaskHolder,
    application,
    settings,
    fFeatureProvider,
    fDeviceOrchestrator
) {
    override val TAG = "SimpleEmulateViewModel"
}
