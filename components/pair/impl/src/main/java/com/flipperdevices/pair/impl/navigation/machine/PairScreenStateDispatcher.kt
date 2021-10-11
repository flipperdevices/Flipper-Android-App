package com.flipperdevices.pair.impl.navigation.machine

import com.flipperdevices.pair.impl.navigation.models.PairScreenState

interface PairScreenStateDispatcher {
    fun invalidateCurrentState(stateChanger: (PairScreenState) -> PairScreenState)
    fun invalidate(state: PairScreenState)
    fun back()
}
