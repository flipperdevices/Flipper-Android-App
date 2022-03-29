package com.flipperdevices.bridge.synchronization.ui.viewmodel

import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.ui.model.ItemSynchronizationState

object ItemSynchronizationStateMapper {
    internal fun getItemSynchronizedState(
        synchronized: Boolean,
        synchronizationState: SynchronizationState
    ): ItemSynchronizationState {
        if (synchronized) {
            return ItemSynchronizationState.SYNCHRONIZED
        }

        if (synchronizationState == SynchronizationState.IN_PROGRESS) {
            return ItemSynchronizationState.IN_PROGRESS
        }

        return ItemSynchronizationState.NOT_SYNCHRONIZED
    }
}
