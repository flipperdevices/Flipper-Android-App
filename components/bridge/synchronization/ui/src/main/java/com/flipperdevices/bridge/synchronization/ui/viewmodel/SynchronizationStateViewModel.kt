package com.flipperdevices.bridge.synchronization.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import javax.inject.Inject

class SynchronizationStateViewModel : ViewModel() {
    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var keysApi: SimpleKeyApi

    /*fun getSynchronizationState(keyPath: FlipperKeyPath): Flow<SynchronizationState> =
        keysApi*/
}
