package com.flipper.pair.impl.navigation.factory

import com.flipper.pair.impl.navigation.models.PairScreenState

interface PairStateStorage {
    fun getSavedPairState(): PairScreenState
    fun markTosPassed()
}