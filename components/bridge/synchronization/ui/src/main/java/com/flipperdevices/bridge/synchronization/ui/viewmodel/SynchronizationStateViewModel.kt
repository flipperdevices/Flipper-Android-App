package com.flipperdevices.bridge.synchronization.ui.viewmodel

import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.ui.model.ItemSynchronizationState
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class SynchronizationStateViewModel @Inject constructor(
    private val synchronizationApi: SynchronizationApi,
    private val keysApi: SimpleKeyApi
) : DecomposeViewModel() {
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
