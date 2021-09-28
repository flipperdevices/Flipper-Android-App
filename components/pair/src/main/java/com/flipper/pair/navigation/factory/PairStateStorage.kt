package com.flipper.pair.navigation.factory

import com.flipper.pair.navigation.models.PairScreenState

interface PairStateStorage {
    fun getSavedPairState(): PairScreenState
    fun markTosPassed()
}