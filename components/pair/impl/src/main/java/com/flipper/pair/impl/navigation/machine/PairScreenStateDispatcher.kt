package com.flipper.pair.impl.navigation.machine

import com.flipper.pair.impl.navigation.models.PairScreenState

interface PairScreenStateDispatcher {
    fun invalidateCurrentState(stateChanger: (PairScreenState) -> PairScreenState)
    fun invalidate(state: PairScreenState)
    fun back()
}
