package com.flipperdevices.bridge.synchronization.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.ui.di.SynchronizationUiComponent
import com.flipperdevices.bridge.synchronization.ui.model.ItemSynchronizationState
import com.flipperdevices.core.di.ComponentHolder
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class SynchronizationStateViewModel : ViewModel() {
    @Inject
    lateinit var synchronizationApi: SynchronizationApi

    @Inject
    lateinit var keysApi: SimpleKeyApi

    init {
        ComponentHolder.component<SynchronizationUiComponent>().inject(this)
    }

    internal fun getSynchronizationState(
        keyPath: FlipperKeyPath
    ): Flow<ItemSynchronizationState> = combine(
        keysApi.getKeyAsFlow(keyPath),
        synchronizationApi.getSynchronizationState()
    ) { flipperKey, synchronizationState ->
        if (flipperKey == null) {
            return@combine ItemSynchronizationState.NOT_SYNCHRONIZED
        }

        return@combine ItemSynchronizationStateMapper.getItemSynchronizedState(
            flipperKey.synchronized,
            synchronizationState
        )
    }
}
