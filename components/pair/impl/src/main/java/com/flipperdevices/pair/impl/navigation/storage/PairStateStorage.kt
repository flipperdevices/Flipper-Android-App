package com.flipperdevices.pair.impl.navigation.storage

import com.flipperdevices.pair.impl.navigation.models.PairScreenState

interface PairStateStorage {
    fun getSavedPairState(): PairScreenState
    fun markTosPassed()
}
