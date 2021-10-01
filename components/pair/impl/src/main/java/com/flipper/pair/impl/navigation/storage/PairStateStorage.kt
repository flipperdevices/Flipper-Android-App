package com.flipper.pair.impl.navigation.storage

import com.flipper.pair.impl.navigation.models.PairScreenState

interface PairStateStorage {
    fun getSavedPairState(): PairScreenState
    fun markTosPassed()
}
