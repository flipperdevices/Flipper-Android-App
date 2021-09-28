package com.flipper.pair.navigation.machine

import com.flipper.pair.navigation.models.PairScreenState

interface PairScreenStateDispatcher {
    fun invalidateCurrentState(stateChanger: (PairScreenState) -> PairScreenState)
    fun invalidate(state: PairScreenState)
    fun back()
}